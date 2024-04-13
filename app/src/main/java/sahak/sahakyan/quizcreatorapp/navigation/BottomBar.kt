package sahak.sahakyan.quizcreatorapp.navigation

import sahak.sahakyan.quizcreatorapp.R


sealed class BottomBar(
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {

    data object Home: BottomBar(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_bottom_home,
        icon_focused = R.drawable.ic_bottom_home_focused
    )

    // for report
    data object Report: BottomBar(
        route = "report",
        title = "Report",
        icon = R.drawable.ic_bottom_report,
        icon_focused = R.drawable.ic_bottom_report_focused
    )

    // for report
    data object Profile: BottomBar(
        route = "profile",
        title = "Profile",
        icon = R.drawable.ic_bottom_profile,
        icon_focused = R.drawable.ic_bottom_profile_focused
    )
}