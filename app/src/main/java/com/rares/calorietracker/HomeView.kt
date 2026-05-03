package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import java.time.LocalDate
import com.rares.calorietracker.DayView
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeView(viewModel: CalorieTrackerViewModel, navController: NavController) {
    val today = LocalDate.now()
    val initialPage = Int.MAX_VALUE / 2
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage > initialPage) {
            pagerState.animateScrollToPage(initialPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        ) { page ->
        val offset = page - initialPage
        val date = today.plusDays(offset.toLong())
        val dayInfo = viewModel.getDayInfo(date)
        DayView(
            dayInfo = dayInfo,
            allTrainingNames = viewModel.typeOfTraining,
            foodList = viewModel.allFoodList,
            navController = navController,
            onPreviousDay = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextDay = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            isToday = page == initialPage
        )
    }
}