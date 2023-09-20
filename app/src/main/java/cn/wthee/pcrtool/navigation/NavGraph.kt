package cn.wthee.pcrtool.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.character.CharacterBasicInfo
import cn.wthee.pcrtool.ui.character.CharacterDetail
import cn.wthee.pcrtool.ui.character.CharacterExtraEquip
import cn.wthee.pcrtool.ui.character.CharacterList
import cn.wthee.pcrtool.ui.character.CharacterSkillLoop
import cn.wthee.pcrtool.ui.character.CharacterStatusCoeCompose
import cn.wthee.pcrtool.ui.character.CharacterStoryDetail
import cn.wthee.pcrtool.ui.character.RankCompare
import cn.wthee.pcrtool.ui.character.RankEquipCount
import cn.wthee.pcrtool.ui.character.RankEquipList
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.equip.EquipMaterialDetail
import cn.wthee.pcrtool.ui.equip.EquipUnitList
import cn.wthee.pcrtool.ui.home.Overview
import cn.wthee.pcrtool.ui.skill.SummonDetail
import cn.wthee.pcrtool.ui.story.StoryPicList
import cn.wthee.pcrtool.ui.theme.colorAlphaBlack
import cn.wthee.pcrtool.ui.theme.colorAlphaWhite
import cn.wthee.pcrtool.ui.theme.myExit
import cn.wthee.pcrtool.ui.theme.myFadeIn
import cn.wthee.pcrtool.ui.theme.myPopExit
import cn.wthee.pcrtool.ui.theme.shapeTop
import cn.wthee.pcrtool.ui.tool.AllCharacterRankEquipCount
import cn.wthee.pcrtool.ui.tool.AllSkillList
import cn.wthee.pcrtool.ui.tool.AllToolMenu
import cn.wthee.pcrtool.ui.tool.BirthdayList
import cn.wthee.pcrtool.ui.tool.CalendarEventList
import cn.wthee.pcrtool.ui.tool.ComicList
import cn.wthee.pcrtool.ui.tool.FreeGachaList
import cn.wthee.pcrtool.ui.tool.GachaList
import cn.wthee.pcrtool.ui.tool.GuildList
import cn.wthee.pcrtool.ui.tool.LeaderTier
import cn.wthee.pcrtool.ui.tool.LeaderboardList
import cn.wthee.pcrtool.ui.tool.MainSettings
import cn.wthee.pcrtool.ui.tool.NewsList
import cn.wthee.pcrtool.ui.tool.TweetList
import cn.wthee.pcrtool.ui.tool.WebsiteList
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleDetail
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleList
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetail
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipDetail
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipDropList
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipList
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipUnitList
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraEquipTravelList
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraEquipTravelQuestDetail
import cn.wthee.pcrtool.ui.tool.mockgacha.MockGacha
import cn.wthee.pcrtool.ui.tool.pvp.PvpSearchCompose
import cn.wthee.pcrtool.ui.tool.quest.AllQuestList
import cn.wthee.pcrtool.ui.tool.quest.RandomEquipArea
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventBossDetail
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventList
import cn.wthee.pcrtool.ui.tool.uniqueequip.UniqueEquipList
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet

/**
 * 导航内容
 */
@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun NavGraph(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    viewModel: NavViewModel,
    actions: NavActions,
) {
    val statusBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()


    ModalBottomSheetLayout(
        modifier = Modifier.padding(top = statusBarHeight),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetShape = shapeTop(),
        bottomSheetNavigator = bottomSheetNavigator
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = NavRoute.HOME,
            enterTransition = { myFadeIn },
            exitTransition = { myExit },
            popEnterTransition = { myFadeIn },
            popExitTransition = { myPopExit }
        ) {

            //首页
            composable(
                route = NavRoute.HOME
            ) {
                //从其它页面返回时（非展开设置时），主按钮初始
                if (navController.currentDestination?.route == NavRoute.HOME
                    && viewModel.fabMainIcon.value != MainIconType.DOWN
                ) {
                    viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                }
                val scrollState = rememberScrollState()
                Overview(actions = actions, scrollState = scrollState)
            }

            //角色列表
            composable(
                route = NavRoute.CHARACTER_LIST
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyGridState()
                CharacterList(scrollState, actions.toCharacterDetail)
            }

            //角色属性详情
            composable(
                route = "${NavRoute.CHARACTER_DETAIL}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                CharacterDetail(unitId = arguments.getInt(NavRoute.UNIT_ID), actions)
            }

            //角色图片详情
            bottomSheet(
                route = "${NavRoute.ALL_PICS}/{${NavRoute.UNIT_ID}}/{${NavRoute.ALL_PICS_TYPE}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.ALL_PICS_TYPE) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)

                StoryPicList(
                    arguments.getInt(NavRoute.UNIT_ID),
                    AllPicsType.getByValue(arguments.getInt(NavRoute.ALL_PICS_TYPE))
                )
            }

            //角色资料
            bottomSheet(
                route = "${NavRoute.CHARACTER_BASIC_INFO}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterBasicInfo(unitId = arguments.getInt(NavRoute.UNIT_ID))
            }

            //角色剧情属性详情
            bottomSheet(
                route = "${NavRoute.CHARACTER_STORY_DETAIL}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterStoryDetail(unitId = arguments.getInt(NavRoute.UNIT_ID))
            }

            //装备列表
            composable(
                route = NavRoute.EQUIP_LIST
            ) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                EquipList(
                    scrollState = scrollState,
                    toEquipDetail = actions.toEquipDetail,
                    toEquipMaterial = actions.toEquipMaterial,
                    toSearchEquipQuest = actions.toSearchEquipQuest
                )
            }

            //ex装备列表
            composable(
                route = NavRoute.TOOL_EXTRA_EQUIP
            ) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ExtraEquipList(
                    scrollState = scrollState,
                    toExtraEquipDetail = actions.toExtraEquipDetail
                )
            }

            //ex装备冒险区域
            composable(
                route = NavRoute.TOOL_TRAVEL_AREA
            ) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ExtraEquipTravelList(
                    scrollState = scrollState,
                    toExtraEquipTravelAreaDetail = actions.toExtraEquipTravelAreaDetail
                )
            }

            //ex装备冒险区域详情
            composable(
                route = "${NavRoute.TOOL_TRAVEL_AREA_DETAIL}/{${NavRoute.TRAVEL_QUEST_ID}}",
                arguments = listOf(navArgument(NavRoute.TRAVEL_QUEST_ID) {
                    type = NavType.IntType
                })
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                ExtraEquipTravelQuestDetail(
                    arguments.getInt(NavRoute.TRAVEL_QUEST_ID),
                    actions.toExtraEquipDetail
                )
            }

            //装备详情
            composable(
                route = "${NavRoute.EQUIP_DETAIL}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                EquipMainInfo(
                    arguments.getInt(NavRoute.EQUIP_ID),
                    toEquipMaterial = actions.toEquipMaterial,
                    toEquipUnit = actions.toEquipUnit
                )
            }

            //装备关联角色
            bottomSheet(
                route = "${NavRoute.TOOL_EQUIP_UNIT}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                EquipUnitList(equipId = arguments.getInt(NavRoute.EQUIP_ID))
            }

            //ex装备详情
            composable(
                route = "${NavRoute.EXTRA_EQUIP_DETAIL}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                ExtraEquipDetail(
                    equipId = arguments.getInt(NavRoute.EQUIP_ID),
                    toExtraEquipUnit = actions.toExtraEquipUnit,
                    toExtraEquipDrop = actions.toExtraEquipDrop
                )
            }

            //ex装备关联角色
            bottomSheet(
                route = "${NavRoute.TOOL_EXTRA_EQUIP_UNIT}/{${NavRoute.EXTRA_EQUIP_CATEGORY}}",
                arguments = listOf(navArgument(NavRoute.EXTRA_EQUIP_CATEGORY) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                ExtraEquipUnitList(category = arguments.getInt(NavRoute.EXTRA_EQUIP_CATEGORY))
            }

            //ex装备掉落信息
            bottomSheet(
                route = "${NavRoute.EXTRA_EQUIP_DROP}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                ExtraEquipDropList(equipId = arguments.getInt(NavRoute.EQUIP_ID))
            }

            //装备素材详情
            bottomSheet(
                route = "${NavRoute.EQUIP_MATERIAL}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                EquipMaterialDetail(arguments.getInt(NavRoute.EQUIP_ID))
            }

            //角色 RANK 装备
            bottomSheet(
                route = "${NavRoute.RANK_EQUIP}/{${NavRoute.UNIT_ID}}/{${NavRoute.RANK}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.RANK) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                RankEquipList(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    currentRank = arguments.getInt(NavRoute.RANK)
                )
            }

            //角色 RANK 对比
            bottomSheet(
                route = "${NavRoute.RANK_COMPARE}/{${NavRoute.UNIT_ID}}/{${NavRoute.MAX_RANK}}/{${NavRoute.LEVEL}}/{${NavRoute.RARITY}}/{${NavRoute.UNIQUE_EQUIP_LEVEL}}/{${NavRoute.UNIQUE_EQUIP_LEVEL2}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.MAX_RANK) {
                    type = NavType.IntType
                }, navArgument(NavRoute.LEVEL) {
                    type = NavType.IntType
                }, navArgument(NavRoute.RARITY) {
                    type = NavType.IntType
                }, navArgument(NavRoute.UNIQUE_EQUIP_LEVEL) {
                    type = NavType.IntType
                }, navArgument(NavRoute.UNIQUE_EQUIP_LEVEL2) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                RankCompare(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    maxRank = arguments.getInt(NavRoute.MAX_RANK),
                    level = arguments.getInt(NavRoute.LEVEL),
                    rarity = arguments.getInt(NavRoute.RARITY),
                    uniqueEquipLevel = arguments.getInt(NavRoute.UNIQUE_EQUIP_LEVEL),
                    uniqueEquipLevel2 = arguments.getInt(NavRoute.UNIQUE_EQUIP_LEVEL2)
                )
            }

            //角色ex装备列表
            composable(
                route = "${NavRoute.CHARACTER_EXTRA_EQUIP_SLOT}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val scrollState = rememberLazyListState()
                val arguments = requireNotNull(it.arguments)
                CharacterExtraEquip(
                    scrollState = scrollState,
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    toExtraEquipDetail = actions.toExtraEquipDetail
                )
            }

            //角色装备统计
            composable(
                route = "${NavRoute.EQUIP_COUNT}/{${NavRoute.UNIT_ID}}/{${NavRoute.MAX_RANK}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.MAX_RANK) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                RankEquipCount(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    maxRank = arguments.getInt(NavRoute.MAX_RANK),
                    actions.toEquipMaterial
                )
            }

            //角色排行
            composable(
                route = NavRoute.TOOL_LEADER
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                LeaderboardList(scrollState, actions.toCharacterDetail)
            }

            //角色排行评级
            composable(
                route = NavRoute.TOOL_LEADER_TIER
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                LeaderTier(scrollState, actions.toCharacterDetail)
            }

            //角色卡池
            composable(
                route = NavRoute.TOOL_GACHA
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyStaggeredGridState()
                GachaList(scrollState, actions.toCharacterDetail, actions.toMockGacha)
            }

            //免费十连
            composable(
                route = NavRoute.TOOL_FREE_GACHA
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyStaggeredGridState()
                FreeGachaList(scrollState)
            }

            //剧情活动
            composable(
                route = NavRoute.TOOL_STORY_EVENT
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyStaggeredGridState()
                StoryEventList(
                    scrollState = scrollState,
                    toCharacterDetail = actions.toCharacterDetail,
                    toEventEnemyDetail = actions.toEventEnemyDetail,
                    toAllPics = actions.toAllPics
                )
            }

            //角色公会
            composable(
                route = NavRoute.TOOL_GUILD
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyStaggeredGridState()
                GuildList(scrollState, actions.toCharacterDetail)
            }

            //公会战
            composable(
                route = NavRoute.TOOL_CLAN,
            ) {
                val scrollState = rememberLazyGridState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ClanBattleList(scrollState, actions.toClanBossInfo)
            }

            //公会战详情
            composable(
                route = "${NavRoute.TOOL_CLAN_BOSS_INFO}/{${NavRoute.TOOL_CLAN_Battle_ID}}/{${NavRoute.TOOL_CLAN_BOSS_INDEX}}/{${NavRoute.TOOL_CLAN_BOSS_PHASE}}",
                arguments = listOf(navArgument(NavRoute.TOOL_CLAN_Battle_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.TOOL_CLAN_BOSS_INDEX) {
                    type = NavType.IntType
                }, navArgument(NavRoute.TOOL_CLAN_BOSS_PHASE) {
                    type = NavType.IntType
                }),
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ClanBattleDetail(
                    arguments.getInt(NavRoute.TOOL_CLAN_Battle_ID),
                    arguments.getInt(NavRoute.TOOL_CLAN_BOSS_INDEX),
                    arguments.getInt(NavRoute.TOOL_CLAN_BOSS_PHASE),
                    actions.toSummonDetail
                )
            }

            //竞技场查询
            composable(
                route = NavRoute.TOOL_PVP
            ) {
                val pagerState = rememberPagerState { 4 }
                val selectListState = rememberLazyGridState()
                val usedListState = rememberLazyGridState()
                val resultListState = rememberLazyGridState()
                val favoritesListState = rememberLazyGridState()
                val historyListState = rememberLazyGridState()

                PvpSearchCompose(
                    floatWindow = false,
                    pagerState = pagerState,
                    selectListState = selectListState,
                    usedListState = usedListState,
                    resultListState = resultListState,
                    favoritesListState = favoritesListState,
                    historyListState = historyListState,
                    toCharacter = actions.toCharacterDetail
                )
            }

            //设置页面
            bottomSheet(
                route = NavRoute.MAIN_SETTINGS
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                MainSettings()
            }

            //公告
            composable(
                route = NavRoute.TOOL_NEWS
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                NewsList()
            }

            //推特信息
            composable(
                route = NavRoute.TWEET
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                TweetList()
            }

            //漫画信息
            composable(
                route = NavRoute.COMIC
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ComicList()
            }


            //技能列表
            composable(
                route = NavRoute.ALL_SKILL
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                AllSkillList(actions.toSummonDetail)
            }

            //战力系数
            bottomSheet(
                route = NavRoute.ATTR_COE
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                CharacterStatusCoeCompose()
            }

            //召唤物信息
            bottomSheet(
                route = "${NavRoute.SUMMON_DETAIL}/{${NavRoute.UNIT_ID}}/{${NavRoute.UNIT_TYPE}}/{${NavRoute.LEVEL}}/{${NavRoute.RANK}}/{${NavRoute.RARITY}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.UNIT_TYPE) {
                    type = NavType.IntType
                }, navArgument(NavRoute.LEVEL) {
                    type = NavType.IntType
                }, navArgument(NavRoute.RANK) {
                    type = NavType.IntType
                }, navArgument(NavRoute.RARITY) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                SummonDetail(
                    id = arguments.getInt(NavRoute.UNIT_ID),
                    unitType = UnitType.getByValue(arguments.getInt(NavRoute.UNIT_TYPE)),
                    level = arguments.getInt(NavRoute.LEVEL),
                    rank = arguments.getInt(NavRoute.RANK),
                    rarity = arguments.getInt(NavRoute.RARITY)
                )
            }

            //技能循环信息
            bottomSheet(
                route = "${NavRoute.CHARACTER_SKILL_LOOP}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterSkillLoop(unitId = arguments.getInt(NavRoute.UNIT_ID), scrollable = true)
            }

            //所有角色所需装备统计
            composable(
                route = NavRoute.ALL_EQUIP
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                AllCharacterRankEquipCount(actions.toEquipMaterial)
            }

            //额外随机装备掉落地区
            composable(
                route = "${NavRoute.TOOL_EQUIP_AREA}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                }),
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                val arguments = requireNotNull(it.arguments)
                RandomEquipArea(
                    arguments.getInt(NavRoute.EQUIP_ID), scrollState
                )
            }

            //更多工具
            composable(
                route = "${NavRoute.TOOL_MORE}/{${NavRoute.TOOL_MORE_EDIT_MODE}}",
                arguments = listOf(navArgument(NavRoute.TOOL_MORE_EDIT_MODE) {
                    type = NavType.BoolType
                }),
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                AllToolMenu(
                    arguments.getBoolean(NavRoute.TOOL_MORE_EDIT_MODE), scrollState, actions
                )
            }

            //模拟抽卡
            composable(
                route = NavRoute.TOOL_MOCK_GACHA
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                MockGacha()
            }

            //生日日程
            composable(
                route = NavRoute.TOOL_BIRTHDAY
            ) {
                val scrollState = rememberLazyStaggeredGridState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                BirthdayList(scrollState, actions.toCharacterDetail)
            }

            //日程
            composable(
                route = NavRoute.TOOL_CALENDAR_EVENT
            ) {
                val scrollState = rememberLazyStaggeredGridState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                CalendarEventList(scrollState)
            }

            //怪物详情信息
            composable(
                route = "${NavRoute.ENEMY_DETAIL}/{${NavRoute.ENEMY_ID}}",
                arguments = listOf(navArgument(NavRoute.ENEMY_ID) {
                    type = NavType.IntType
                })
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                EnemyDetail(
                    arguments.getInt(NavRoute.ENEMY_ID),
                    actions.toSummonDetail
                )
            }

            //活动剧情怪物详情信息
            composable(
                route = "${NavRoute.EVENT_ENEMY_DETAIL}/{${NavRoute.ENEMY_ID}}",
                arguments = listOf(navArgument(NavRoute.ENEMY_ID) {
                    type = NavType.IntType
                })
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                StoryEventBossDetail(
                    arguments.getInt(NavRoute.ENEMY_ID),
                    actions.toSummonDetail
                )
            }

            //网站
            composable(
                route = NavRoute.PCR_WEBSITE
            ) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                WebsiteList(scrollState)
            }

            //主线地图
            composable(
                route = NavRoute.TOOL_ALL_QUEST
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                AllQuestList()
            }

            //装备掉落搜索
            bottomSheet(
                route = "${NavRoute.TOOL_ALL_QUEST}/{${NavRoute.SEARCH_EQUIP_IDS}}",
                arguments = listOf(navArgument(NavRoute.SEARCH_EQUIP_IDS) {
                    type = NavType.StringType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                AllQuestList(
                    searchEquipIds = arguments.getString(NavRoute.SEARCH_EQUIP_IDS) ?: ""
                )
            }

            //专用装备列表
            composable(
                route = NavRoute.UNIQUE_EQUIP_LIST
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                UniqueEquipList(
                    toUniqueEquipDetail = actions.toUniqueEquipDetail
                )
            }

            //专用装备属性详情
            composable(
                route = "${NavRoute.UNIQUE_EQUIP_DETAIL}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val showDetail = remember {
                    mutableStateOf(false)
                }
                CharacterDetail(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    actions,
                    showDetailState = showDetail
                )
            }
        }
    }
}

/**
 * 导航
 */
class NavActions(navController: NavHostController) {

    /**
     * 角色列表
     */
    val toCharacterList: () -> Unit = {
        navController.navigate(NavRoute.CHARACTER_LIST)
    }

    /**
     * 角色详情
     */
    val toCharacterDetail: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${NavRoute.CHARACTER_DETAIL}/${unitId}")
    }

    /**
     * 角色图片详情
     */
    val toAllPics: (Int, Int) -> Unit = { unitId: Int, type: Int ->
        navController.navigate("${NavRoute.ALL_PICS}/${unitId}/${type}")
    }

    /**
     * 装备详情
     */
    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${NavRoute.EQUIP_DETAIL}/${equipId}")
    }

    /**
     * 装备详情关联角色
     */
    val toEquipUnit: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${NavRoute.TOOL_EQUIP_UNIT}/${equipId}")
    }

    /**
     * ex装备详情
     */
    val toExtraEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${NavRoute.EXTRA_EQUIP_DETAIL}/${equipId}")
    }

    /**
     * ex装备掉落
     */
    val toExtraEquipDrop: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${NavRoute.EXTRA_EQUIP_DROP}/${equipId}")
    }

    /**
     * ex装备详情关联角色
     */
    val toExtraEquipUnit: (Int) -> Unit = { category: Int ->
        navController.navigate("${NavRoute.TOOL_EXTRA_EQUIP_UNIT}/${category}")
    }

    /**
     * 装备素材详情
     */
    val toEquipMaterial: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${NavRoute.EQUIP_MATERIAL}/${equipId}")
    }

    /**
     * 角色资料
     */
    val toCharacterBasicInfo: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${NavRoute.CHARACTER_BASIC_INFO}/${unitId}")
    }

    /**
     * 角色剧情属性详情
     */
    val toCharacterStoryDetail: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${NavRoute.CHARACTER_STORY_DETAIL}/${unitId}")
    }

    /**
     * 角色 RANK 装备
     */
    val toCharacterRankEquip: (Int, Int) -> Unit = { unitId: Int, currentRank: Int ->
        navController.navigate("${NavRoute.RANK_EQUIP}/${unitId}/${currentRank}")
    }

    /**
     * 角色 RANK 对比
     */
    val toCharacterRankCompare: (Int, Int, Int, Int, Int, Int) -> Unit =
        { unitId: Int, maxRank: Int, level: Int, rarity: Int, uniqueEquipLevel: Int, uniqueEquipLevel2: Int ->
            navController.navigate("${NavRoute.RANK_COMPARE}/${unitId}/${maxRank}/${level}/${rarity}/${uniqueEquipLevel}/${uniqueEquipLevel2}")
        }


    /**
     * 角色ex装备列表
     */
    val toCharacterExtraEquip: (Int) -> Unit = { unitId ->
        navController.navigate("${NavRoute.CHARACTER_EXTRA_EQUIP_SLOT}/${unitId}")
    }


    /**
     * 角装备统计
     */
    val toCharacterEquipCount: (Int, Int) -> Unit = { unitId: Int, maxRank: Int ->
        navController.navigate("${NavRoute.EQUIP_COUNT}/${unitId}/${maxRank}")
    }


    /**
     * 公会战 BOSS
     */
    val toClanBossInfo: (Int, Int, Int) -> Unit = { clanId: Int, index: Int, phase: Int ->
        navController.navigate("${NavRoute.TOOL_CLAN_BOSS_INFO}/${clanId}/${index}/${phase}")
    }

    /**
     * 卡池
     */
    val toGacha = {
        navController.navigate(NavRoute.TOOL_GACHA)
    }

    /**
     * 免费十连
     */
    val toFreeGacha = {
        navController.navigate(NavRoute.TOOL_FREE_GACHA)
    }

    /**
     * 公会战
     */
    val toClan = {
        navController.navigate(NavRoute.TOOL_CLAN)
    }

    /**
     * 剧情活动
     */
    val toStoryEvent = {
        navController.navigate(NavRoute.TOOL_STORY_EVENT)
    }

    /**
     * 角色公会
     */
    val toGuild = {
        navController.navigate(NavRoute.TOOL_GUILD)
    }

    /**
     * 公告
     */
    val toNews: () -> Unit = {
        navController.navigate(NavRoute.TOOL_NEWS)
    }

    /**
     * 竞技场
     */
    val toPvp = {
        navController.navigate(NavRoute.TOOL_PVP)
    }

    /**
     * 排行
     */
    val toLeader = {
        navController.navigate(NavRoute.TOOL_LEADER)
    }

    /**
     * 装备列表
     */
    val toEquipList = {
        navController.navigate(NavRoute.EQUIP_LIST)
    }

    /**
     * ex装备列表
     */
    val toExtraEquipList = {
        navController.navigate(NavRoute.TOOL_EXTRA_EQUIP)
    }

    /**
     * ex装备冒险区域
     */
    val toExtraEquipTravelAreaList = {
        navController.navigate(NavRoute.TOOL_TRAVEL_AREA)
    }

    /**
     * ex装备冒险区域详情
     */
    val toExtraEquipTravelAreaDetail: (Int) -> Unit = { questId ->
        navController.navigate("${NavRoute.TOOL_TRAVEL_AREA_DETAIL}/${questId}")

    }

    /**
     * 设置
     */
    val toSetting = {
        navController.navigate(NavRoute.MAIN_SETTINGS)
    }

    /**
     * 推特
     */
    val toTweetList = {
        navController.navigate(NavRoute.TWEET)
    }

    /**
     * 漫画
     */
    val toComicList = {
        navController.navigate(NavRoute.COMIC)
    }

    /**
     * 技能列表
     */
    val toAllSkillList = {
        navController.navigate(NavRoute.ALL_SKILL)
    }

    /**
     * 战力系数
     */
    val toCoe = {
        navController.navigate(NavRoute.ATTR_COE)
    }

    /**
     * 召唤物信息
     */
    val toSummonDetail: (Int, Int, Int, Int, Int) -> Unit =
        { unitId, unitType, level, rank, rarity ->
            navController.navigate("${NavRoute.SUMMON_DETAIL}/${unitId}/${unitType}/${level}/${rank}/${rarity}")
        }

    /**
     * 装备统计
     */
    val toAllEquipList = {
        navController.navigate(NavRoute.ALL_EQUIP)
    }

    /**
     * 额外随机装备掉落地区
     */
    val toRandomEquipArea: (Int) -> Unit = { equipId ->
        navController.navigate("${NavRoute.TOOL_EQUIP_AREA}/${equipId}")
    }

    /**
     * 更多工具
     */
    val toToolMore: (Boolean) -> Unit = { editMode ->
        navController.navigate("${NavRoute.TOOL_MORE}/${editMode}")
    }

    /**
     * 模拟抽卡
     */
    val toMockGacha = {
        navController.navigate(NavRoute.TOOL_MOCK_GACHA)
    }

    /**
     * 生日一览
     */
    val toBirthdayList = {
        navController.navigate(NavRoute.TOOL_BIRTHDAY)
    }

    /**
     * 活动一览
     */
    val toCalendarEventList = {
        navController.navigate(NavRoute.TOOL_CALENDAR_EVENT)
    }

    /**
     * 角色技能循环
     */
    val toCharacterSkillLoop: (Int) -> Unit = { unitId ->
        navController.navigate("${NavRoute.CHARACTER_SKILL_LOOP}/${unitId}")
    }

    /**
     * 活动剧情怪物详情信息
     */
    val toEventEnemyDetail: (Int) -> Unit = { enemyId ->
        navController.navigate("${NavRoute.EVENT_ENEMY_DETAIL}/${enemyId}")
    }

    /**
     * 网站聚合
     */
    val toWebsiteList = {
        navController.navigate(NavRoute.PCR_WEBSITE)
    }

    /**
     * 角色评级
     */
    val toLeaderTier = {
        navController.navigate(NavRoute.TOOL_LEADER_TIER)
    }

    /**
     * 主线地图
     */
    val toAllQuest = {
        navController.navigate(NavRoute.TOOL_ALL_QUEST)
    }

    /**
     * 装备掉落搜索
     */
    val toSearchEquipQuest: (String) -> Unit = { searchEquipIds ->
        navController.navigate("${NavRoute.TOOL_ALL_QUEST}/${searchEquipIds}")
    }

    /**
     * 专用装备列表
     */
    val toUniqueEquipList = {
        navController.navigate(NavRoute.UNIQUE_EQUIP_LIST)
    }

    /**
     * 专用装备详情
     */
    val toUniqueEquipDetail: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${NavRoute.UNIQUE_EQUIP_DETAIL}/${unitId}")
    }

}