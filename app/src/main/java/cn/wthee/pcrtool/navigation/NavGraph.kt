package cn.wthee.pcrtool.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SummonProperty
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainSettings
import cn.wthee.pcrtool.ui.character.CharacterListScreen
import cn.wthee.pcrtool.ui.character.detail.CharacterDetailScreen
import cn.wthee.pcrtool.ui.character.equipcount.RankEquipCountScreen
import cn.wthee.pcrtool.ui.character.extraequip.CharacterExtraEquipScreen
import cn.wthee.pcrtool.ui.character.filter.CharacterListFilterScreen
import cn.wthee.pcrtool.ui.character.profile.CharacterBasicInfo
import cn.wthee.pcrtool.ui.character.rankcompare.RankCompareScreen
import cn.wthee.pcrtool.ui.character.rankequip.RankEquipListScreen
import cn.wthee.pcrtool.ui.character.skillloop.CharacterSkillLoopScreen
import cn.wthee.pcrtool.ui.character.statuscoe.CharacterStatusCoeScreen
import cn.wthee.pcrtool.ui.character.story.CharacterStoryAttrScreen
import cn.wthee.pcrtool.ui.equip.EquipListScreen
import cn.wthee.pcrtool.ui.equip.detail.EquipDetailScreen
import cn.wthee.pcrtool.ui.equip.drop.EquipMaterialDropInfoScreen
import cn.wthee.pcrtool.ui.equip.filter.EquipListFilterScreen
import cn.wthee.pcrtool.ui.equip.unit.EquipUnitListScreen
import cn.wthee.pcrtool.ui.home.Overview
import cn.wthee.pcrtool.ui.media.PictureScreen
import cn.wthee.pcrtool.ui.media.VideoScreen
import cn.wthee.pcrtool.ui.skill.summon.SkillSummonScreen
import cn.wthee.pcrtool.ui.theme.enterTransition
import cn.wthee.pcrtool.ui.theme.exitTransition
import cn.wthee.pcrtool.ui.theme.maskAlpha
import cn.wthee.pcrtool.ui.theme.shapeTop
import cn.wthee.pcrtool.ui.tool.AllToolMenuScreen
import cn.wthee.pcrtool.ui.tool.birthday.BirthdayListScreen
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleDetailScreen
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleListScreen
import cn.wthee.pcrtool.ui.tool.comic.ComicListScreen
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen
import cn.wthee.pcrtool.ui.tool.event.CalendarEventListScreen
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipList
import cn.wthee.pcrtool.ui.tool.extraequip.detail.ExtraEquipDetail
import cn.wthee.pcrtool.ui.tool.extraequip.drop.ExtraEquipDropListScreen
import cn.wthee.pcrtool.ui.tool.extraequip.filter.ExtraEquipListFilterScreen
import cn.wthee.pcrtool.ui.tool.extraequip.unit.ExtraEquipUnitListScreen
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraTravelDetailScreen
import cn.wthee.pcrtool.ui.tool.extratravel.ExtraTravelListScreen
import cn.wthee.pcrtool.ui.tool.freegacha.FreeGachaListScreen
import cn.wthee.pcrtool.ui.tool.gacha.GachaListScreen
import cn.wthee.pcrtool.ui.tool.guild.GuildListScreen
import cn.wthee.pcrtool.ui.tool.leaderboard.LeaderboardScreen
import cn.wthee.pcrtool.ui.tool.leadertier.LeaderTierScreen
import cn.wthee.pcrtool.ui.tool.loadcomic.LoadComicScreen
import cn.wthee.pcrtool.ui.tool.mockgacha.MockGachaScreen
import cn.wthee.pcrtool.ui.tool.news.NewsScreen
import cn.wthee.pcrtool.ui.tool.pvp.PvpSearchScreen
import cn.wthee.pcrtool.ui.tool.quest.QuestListScreen
import cn.wthee.pcrtool.ui.tool.randomdrop.RandomDropAreaListScreen
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventBossDetail
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventListScreen
import cn.wthee.pcrtool.ui.tool.talent.UnitTalentListScreen
import cn.wthee.pcrtool.ui.tool.tweet.TweetList
import cn.wthee.pcrtool.ui.tool.uniqueequip.UniqueEquipListScreen
import cn.wthee.pcrtool.ui.tool.website.WebsiteScreen
import cn.wthee.pcrtool.utils.JsonUtil
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet


//返回上一级
@OptIn(ExperimentalMaterialApi::class)
suspend fun navigateUpSheet() {
    MainActivity.navSheetState.hide()
}

//返回上一级
fun navigateUp() {
    MainActivity.navController.navigateUp()
}

/**
 * 设置当前页面数据
 *
 * @param prev 设置上一页面数据
 */
fun <T> setData(key: String, value: T?, prev: Boolean = false) {
    if (prev) {
        MainActivity.navController.previousBackStackEntry?.savedStateHandle?.set(
            key,
            value
        )
    } else {
        MainActivity.navController.currentBackStackEntry?.savedStateHandle?.set(
            key,
            value
        )
    }
}

/**
 * 获取当前页面数据
 */
fun <T> getData(key: String, prev: Boolean = false): T? {
    return if (prev) {
        MainActivity.navController.previousBackStackEntry?.savedStateHandle?.get<T>(key)
    } else {
        MainActivity.navController.currentBackStackEntry?.savedStateHandle?.get<T>(key)
    }
}

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
    actions: NavActions,
) {

    ModalBottomSheetLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding(),
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = maskAlpha),
        sheetShape = shapeTop(),
        bottomSheetNavigator = bottomSheetNavigator
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = NavRoute.HOME,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {

            //首页
            composable(
                route = NavRoute.HOME
            ) {
                Overview(actions = actions)
            }

            //角色列表
            composable(
                route = NavRoute.CHARACTER_LIST
            ) {
                CharacterListScreen(
                    toCharacterDetail = actions.toCharacterDetail,
                    toFilterCharacter = actions.toFilterCharacter
                )
            }
            //角色列表筛选
            bottomSheet(
                route = "${NavRoute.FILTER_CHARACTER}/{${NavRoute.FILTER_DATA}}",
                arguments = listOf(navArgument(NavRoute.FILTER_DATA) {
                    type = NavType.StringType
                })
            ) {
                CharacterListFilterScreen()
            }

            //角色属性详情
            composable(
                route = "${NavRoute.CHARACTER_DETAIL}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                CharacterDetailScreen(actions)
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
                PictureScreen()
            }

            //剧情活动图片详情
            bottomSheet(
                route = "${NavRoute.ALL_STORY_EVENT_PICS}/{${NavRoute.STORY_ID}}/{${NavRoute.ORIGINAL_EVENT_ID}}/{${NavRoute.EVENT_ID}}/{${NavRoute.ALL_PICS_TYPE}}",
                arguments = listOf(navArgument(NavRoute.STORY_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.ORIGINAL_EVENT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.EVENT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.ALL_PICS_TYPE) {
                    type = NavType.IntType
                })
            ) {
                PictureScreen()
            }

            //角色资料
            bottomSheet(
                route = "${NavRoute.CHARACTER_BASIC_INFO}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                CharacterBasicInfo()
            }

            //角色剧情属性详情
            bottomSheet(
                route = "${NavRoute.CHARACTER_STORY_DETAIL}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                CharacterStoryAttrScreen()
            }

            //角色相关视频
            bottomSheet(
                route = "${NavRoute.CHARACTER_VIDEO}/{${NavRoute.UNIT_ID}}/{${NavRoute.CHARACTER_VIDEO_TYPE}}",
                arguments = listOf(
                    navArgument(NavRoute.UNIT_ID) {
                        type = NavType.IntType
                    },
                    navArgument(NavRoute.CHARACTER_VIDEO_TYPE) {
                        type = NavType.IntType
                    },
                )
            ) {
                val arguments = requireNotNull(it.arguments)

                VideoScreen(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    videoTypeValue = arguments.getInt(NavRoute.CHARACTER_VIDEO_TYPE)
                )
            }

            //装备列表
            composable(
                route = NavRoute.EQUIP_LIST
            ) {
                EquipListScreen(
                    toEquipDetail = actions.toEquipDetail,
                    toEquipMaterial = actions.toEquipMaterial,
                    toSearchEquipQuest = actions.toSearchEquipQuest,
                    toFilterEquip = actions.toFilterEquip,
                )
            }

            //装备列表筛选
            bottomSheet(
                route = "${NavRoute.FILTER_EQUIP}/{${NavRoute.FILTER_DATA}}",
                arguments = listOf(navArgument(NavRoute.FILTER_DATA) {
                    type = NavType.StringType
                })
            ) {
                EquipListFilterScreen()
            }

            //ex装备列表
            composable(
                route = NavRoute.TOOL_EXTRA_EQUIP
            ) {
                ExtraEquipList(
                    toExtraEquipDetail = actions.toExtraEquipDetail,
                    toFilterExtraEquip = actions.toFilterExtraEquip,
                )
            }

            //ex装备列表筛选
            bottomSheet(
                route = "${NavRoute.FILTER_EXTRA_EQUIP}/{${NavRoute.FILTER_DATA}}",
                arguments = listOf(navArgument(NavRoute.FILTER_DATA) {
                    type = NavType.StringType
                })
            ) {
                ExtraEquipListFilterScreen()
            }

            //ex装备冒险区域
            composable(
                route = NavRoute.TOOL_TRAVEL_AREA
            ) {
                ExtraTravelListScreen(
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
                ExtraTravelDetailScreen(
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
                val arguments = requireNotNull(it.arguments)
                EquipDetailScreen(
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
                EquipUnitListScreen()
            }

            //ex装备详情
            composable(
                route = "${NavRoute.EXTRA_EQUIP_DETAIL}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
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
                ExtraEquipUnitListScreen()
            }

            //ex装备掉落信息
            bottomSheet(
                route = "${NavRoute.EXTRA_EQUIP_DROP}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                ExtraEquipDropListScreen(equipId = arguments.getInt(NavRoute.EQUIP_ID))
            }

            //装备素材详情
            bottomSheet(
                route = "${NavRoute.EQUIP_MATERIAL}/{${NavRoute.EQUIP_ID}}/{${NavRoute.EQUIP_NAME}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.EQUIP_NAME) {
                    type = NavType.StringType
                })
            ) {
                EquipMaterialDropInfoScreen()
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
                RankEquipListScreen()
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
                RankCompareScreen()
            }

            //角色ex装备列表
            composable(
                route = "${NavRoute.CHARACTER_EXTRA_EQUIP_SLOT}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                CharacterExtraEquipScreen(
                    toExtraEquipDetail = actions.toExtraEquipDetail
                )
            }

            //角色装备统计
            composable(
                route = "${NavRoute.EQUIP_COUNT}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                RankEquipCountScreen(
                    actions.toEquipMaterial
                )
            }

            //全角色装备统计
            composable(
                route = NavRoute.ALL_EQUIP
            ) {
                RankEquipCountScreen(
                    actions.toEquipMaterial
                )
            }

            //角色排行
            composable(
                route = NavRoute.TOOL_LEADER
            ) {
                LeaderboardScreen(actions.toCharacterDetail)
            }

            //角色排行评级
            composable(
                route = NavRoute.TOOL_LEADER_TIER
            ) {
                LeaderTierScreen(actions.toCharacterDetail)
            }

            //角色卡池
            composable(
                route = NavRoute.TOOL_GACHA
            ) {
                GachaListScreen(actions.toCharacterDetail, actions.toMockGachaFromList)
            }

            //免费十连
            composable(
                route = NavRoute.TOOL_FREE_GACHA
            ) {
                FreeGachaListScreen()
            }

            //剧情活动
            composable(
                route = NavRoute.TOOL_STORY_EVENT
            ) {
                StoryEventListScreen(
                    toCharacterDetail = actions.toCharacterDetail,
                    toEventEnemyDetail = actions.toEventEnemyDetail,
                    toAllStoryEventPics = actions.toAllStoryEventPics
                )
            }

            //角色公会
            composable(
                route = NavRoute.TOOL_GUILD
            ) {
                GuildListScreen(actions.toCharacterDetail)
            }

            //公会战
            composable(
                route = NavRoute.TOOL_CLAN,
            ) {
                ClanBattleListScreen(actions.toClanBossInfo)
            }

            //公会战详情
            composable(
                route = "${NavRoute.TOOL_CLAN_BOSS_INFO}/{${NavRoute.CLAN_BATTLE_PROPERTY}}",
                arguments = listOf(navArgument(NavRoute.CLAN_BATTLE_PROPERTY) {
                    type = NavType.StringType
                }),
            ) {
                ClanBattleDetailScreen(
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

                PvpSearchScreen(
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
                MainSettings()
            }

            //公告
            composable(
                route = NavRoute.TOOL_NEWS
            ) {
                NewsScreen()
            }

            //推特信息
            composable(
                route = NavRoute.TWEET
            ) {
                TweetList()
            }

            //漫画信息
            composable(
                route = NavRoute.COMIC
            ) {
                ComicListScreen()
            }

            //战力系数
            bottomSheet(
                route = NavRoute.ATTR_COE
            ) {
                CharacterStatusCoeScreen()
            }

            //召唤物信息
            bottomSheet(
                route = "${NavRoute.SUMMON_DETAIL}/{${NavRoute.SUMMON_PROPERTY}}",
                arguments = listOf(navArgument(NavRoute.SUMMON_PROPERTY) {
                    type = NavType.StringType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                val summonProperty =
                    JsonUtil.fromJson<SummonProperty>(arguments.getString(NavRoute.SUMMON_PROPERTY))
                summonProperty?.let {
                    SkillSummonScreen(
                        id = summonProperty.id,
                        unitType = UnitType.getByValue(summonProperty.type),
                        level = summonProperty.level,
                        rank = summonProperty.rank,
                        rarity = summonProperty.rarity
                    )
                }
            }

            //技能循环信息
            bottomSheet(
                route = "${NavRoute.CHARACTER_SKILL_LOOP}/{${NavRoute.UNIT_ID}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterSkillLoopScreen(
                    unitId = arguments.getInt(NavRoute.UNIT_ID),
                    scrollable = true
                )
            }

            //额外随机装备掉落地区
            composable(
                route = "${NavRoute.TOOL_EQUIP_AREA}/{${NavRoute.EQUIP_ID}}",
                arguments = listOf(navArgument(NavRoute.EQUIP_ID) {
                    type = NavType.IntType
                }),
            ) {
                val arguments = requireNotNull(it.arguments)
                RandomDropAreaListScreen(
                    arguments.getInt(NavRoute.EQUIP_ID)
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
                AllToolMenuScreen(
                    arguments.getBoolean(NavRoute.TOOL_MORE_EDIT_MODE), actions
                )
            }

            //模拟抽卡
            composable(
                route = NavRoute.TOOL_MOCK_GACHA,
            ) {
                MockGachaScreen()
            }

            //模拟抽卡
            composable(
                route = "${NavRoute.TOOL_MOCK_GACHA_FROM_LIST}/{${NavRoute.MOCK_GACHA_TYPE}}/{${NavRoute.PICKUP_LIST}}",
                arguments = listOf(
                    navArgument(NavRoute.MOCK_GACHA_TYPE) {
                        type = NavType.IntType
                    },
                    navArgument(NavRoute.PICKUP_LIST) {
                        type = NavType.StringType
                    }
                )
            ) {
                MockGachaScreen()
            }

            //生日日程
            composable(
                route = NavRoute.TOOL_BIRTHDAY
            ) {
                BirthdayListScreen(actions.toCharacterDetail)
            }

            //日程
            composable(
                route = NavRoute.TOOL_CALENDAR_EVENT
            ) {
                CalendarEventListScreen()
            }

            //怪物详情信息
            composable(
                route = "${NavRoute.ENEMY_DETAIL}/{${NavRoute.ENEMY_ID}}",
                arguments = listOf(navArgument(NavRoute.ENEMY_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                EnemyDetailScreen(
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
                WebsiteScreen()
            }

            //主线地图
            composable(
                route = NavRoute.TOOL_ALL_QUEST
            ) {
                QuestListScreen()
            }

            //装备掉落搜索
            bottomSheet(
                route = "${NavRoute.TOOL_ALL_QUEST}/{${NavRoute.SEARCH_EQUIP_IDS}}",
                arguments = listOf(navArgument(NavRoute.SEARCH_EQUIP_IDS) {
                    type = NavType.StringType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                QuestListScreen(
                    searchEquipIds = arguments.getString(NavRoute.SEARCH_EQUIP_IDS) ?: ""
                )
            }

            //专用装备列表
            composable(
                route = NavRoute.UNIQUE_EQUIP_LIST
            ) {
                UniqueEquipListScreen(
                    toUniqueEquipDetail = actions.toUniqueEquipDetail
                )
            }

            //专用装备属性详情
            composable(
                route = "${NavRoute.UNIQUE_EQUIP_DETAIL}/{${NavRoute.UNIT_ID}}/{${NavRoute.SHOW_ALL_INFO}}",
                arguments = listOf(navArgument(NavRoute.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(NavRoute.SHOW_ALL_INFO) {
                    type = NavType.BoolType
                })
            ) {
                CharacterDetailScreen(actions)
            }

            //过场漫画列表
            composable(
                route = NavRoute.LOAD_COMIC_LIST
            ) {
                LoadComicScreen()
            }

            //角色天赋列表
            composable(
                route = NavRoute.TALENT_LIST
            ) {
                UnitTalentListScreen(
                    toCharacterDetail = actions.toCharacterDetail
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
     * 剧情活动图片详情
     */
    val toAllStoryEventPics: (Int, Int, Int, Int) -> Unit =
        { storyId: Int, originalEventId: Int, eventId: Int, type: Int ->
            navController.navigate("${NavRoute.ALL_STORY_EVENT_PICS}/${storyId}/${originalEventId}/${eventId}/${type}")
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
    val toEquipMaterial: (Int, String) -> Unit = { equipId, equipName ->
        navController.navigate("${NavRoute.EQUIP_MATERIAL}/${equipId}/${equipName}")
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
     * 角色相关视频
     */
    val toCharacterVideo: (Int, Int) -> Unit = { unitId: Int, videoType: Int ->
        navController.navigate("${NavRoute.CHARACTER_VIDEO}/${unitId}/${videoType}")
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
    val toCharacterEquipCount: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${NavRoute.EQUIP_COUNT}/${unitId}")
    }


    /**
     * 公会战 BOSS
     */
    val toClanBossInfo: (String) -> Unit =
        { property ->
            navController.navigate("${NavRoute.TOOL_CLAN_BOSS_INFO}/${property}")
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
     * 战力系数
     */
    val toCoe = {
        navController.navigate(NavRoute.ATTR_COE)
    }

    /**
     * 召唤物信息
     */
    val toSummonDetail: (String) -> Unit = { property ->
        navController.navigate("${NavRoute.SUMMON_DETAIL}/${property}")
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
     * 模拟抽卡（从卡池列表跳转）
     */
    val toMockGachaFromList: (Int, String) -> Unit = { type, list ->
        navController.navigate("${NavRoute.TOOL_MOCK_GACHA_FROM_LIST}/${type}/${list}")
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
        navController.navigate("${NavRoute.UNIQUE_EQUIP_DETAIL}/${unitId}/${false}")
    }

    /**
     * 角色筛选
     */
    val toFilterCharacter: (String) -> Unit = { filter ->
        navController.navigate("${NavRoute.FILTER_CHARACTER}/${filter}")
    }

    /**
     * 装备筛选
     */
    val toFilterEquip: (String) -> Unit = { filter ->
        navController.navigate("${NavRoute.FILTER_EQUIP}/${filter}")
    }

    /**
     * ex装备筛选
     */
    val toFilterExtraEquip: (String) -> Unit = { filter ->
        navController.navigate("${NavRoute.FILTER_EXTRA_EQUIP}/${filter}")
    }

    /**
     * 过场漫画列表
     */
    val toLoadComicList = {
        navController.navigate(NavRoute.LOAD_COMIC_LIST)
    }


    /**
     * 角色天赋列表
     */
    val toUnitTalentList = {
        navController.navigate(NavRoute.TALENT_LIST)
    }
}