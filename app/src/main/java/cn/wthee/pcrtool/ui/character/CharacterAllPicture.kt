package cn.wthee.pcrtool.ui.character

import android.Manifest
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.requestPermissions
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.CommonSpacer
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import coil.Coil
import coil.request.ImageRequest
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


/**
 * 角色图片
 *
 *
 * @param unitId 角色编号
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun CharacterAllPicture(unitId: Int) {
    //角色所有图片链接
    val picUrls = CharacterIdUtil.getAllPicUrl(unitId, MainActivity.r6Ids.contains(unitId))
    val loaded = arrayListOf<Boolean>()
    val drawables = arrayListOf<Drawable?>()
    picUrls.forEach { _ ->
        loaded.add(false)
        drawables.add(null)
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = picUrls.size)
    val icons = CharacterIdUtil.getAllIconUrl(unitId = unitId, MainActivity.r6Ids.contains(unitId))
    //权限
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = Dimen.mediuPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pagerIndex ->
                val request = ImageRequest.Builder(context)
                    .data(picUrls[pagerIndex])
                    .build()
                coroutineScope.launch {
                    val image = Coil.imageLoader(context).execute(request).drawable
                    drawables[pagerIndex] = image
                }
                val painter = rememberCoilPainter(request = picUrls[pagerIndex])
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = when (painter.loadState) {
                            is ImageLoadState.Success -> {
                                loaded[pagerIndex] = true
                                painter
                            }
                            is ImageLoadState.Error -> rememberCoilPainter(request = R.drawable.error)
                            else -> rememberCoilPainter(request = R.drawable.load)
                        },
                        contentDescription = null,
                    )
                }
            }
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
                divider = {
                    TabRowDefaults.Divider(color = Color.Transparent)
                },
            ) {
                for (tabIndex in icons.indices) {
                    Tab(
                        modifier = Modifier.padding(bottom = Dimen.mediuPadding),
                        icon = {
                            IconCompose(data = icons[tabIndex])
                        },
                        selected = pagerState.currentPage == tabIndex,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(tabIndex)
                                VibrateUtil(context).single()
                            }
                        },
                    )
                }
            }
            CommonSpacer()
        }
        val unLoadToast = stringResource(id = R.string.wait_pic_load)
        FabCompose(
            iconType = MainIconType.DWONLOAD,
            text = stringResource(id = R.string.download_pic),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            val index = pagerState.currentPage
            if (loaded[index]) {
                //权限校验
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasPermissions(
                        context,
                        permissions
                    )
                ) {
                    requestPermissions(context as Activity, permissions, 1)
                } else {
                    //fixme 保存时卡顿
                    drawables[index]?.let {
                        ImageDownloadHelper(context).save(
                            (it as BitmapDrawable).bitmap,
                            "${unitId}_${index}.jpg"
                        )
                    }
                }
            } else {
                ToastUtil.short(unLoadToast)
            }
        }
    }

}

