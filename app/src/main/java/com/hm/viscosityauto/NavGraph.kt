package com.hm.viscosityauto

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.asi.nav.Destination
import com.asi.nav.NavigationEffect
import com.hm.viscosityauto.ui.page.AdminPage
import com.hm.viscosityauto.ui.page.AdvParamPage
import com.hm.viscosityauto.ui.page.AuditPage
import com.hm.viscosityauto.ui.page.CleanPage
import com.hm.viscosityauto.ui.page.DebugPage
import com.hm.viscosityauto.ui.page.HelpPage
import com.hm.viscosityauto.ui.page.HistoryPage
import com.hm.viscosityauto.ui.page.HomePage
import com.hm.viscosityauto.ui.page.LoginPage
import com.hm.viscosityauto.ui.page.ManagerPage
import com.hm.viscosityauto.ui.page.ParamPage
import com.hm.viscosityauto.ui.page.SettingPage
import com.hm.viscosityauto.ui.page.SplashPage
import com.hm.viscosityauto.ui.page.TestPage
import com.hm.viscosityauto.vm.LANGUAGE_ZH
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.TestVM
import java.util.Locale


object MainPageRoute : Destination("mainPage")
object HistoryPageRoute : Destination("historyPage")
object SettingPageRoute : Destination("settingPage")
object HelpPageRoute : Destination("helpPage")
object LoginPageRoute : Destination("loginPage")

object SplashPageRoute : Destination("splashPage")

object AuditPageRoute : Destination("auditPage")
object TestPageRoute : Destination("testPage")

object ManagerPageRoute : Destination("managerPage")

object DebugPageRoute : Destination("DebugPage")


object AdminPageRoute : Destination("admin")
object DeviceParamPageRoute : Destination("deviceParam")

object AvdParamPageRoute : Destination("avdParamPage")

object CleanPageRoute : Destination("cleanPage")



@Composable
fun NavGraph(vm: MainVM = viewModel()) {
    val context = LocalContext.current

    DisposableEffect(Unit)
    {
        vm.init(context)
        getVersion(context)?.let { vm.setVersion(it) }
        onDispose {  }
    }

    val startPage = SplashPageRoute.route

    SetLanguage(vm.language.value)
    NavigationEffect(
        startDestination = startPage,
    ) {

        composable(SplashPageRoute.route) {
            SplashPage(vm = vm)
        }

        composable(MainPageRoute.route) {
            HomePage(vm = vm)
        }

        composable(TestPageRoute.route) {
            TestPage()
        }

        composable(SettingPageRoute.route) {
            SettingPage(vm = vm)
        }

        composable(HistoryPageRoute.route) {
            HistoryPage()
        }

        composable(HelpPageRoute.route) {
            if (vm.language.value== LANGUAGE_ZH){
                HelpPage(vm.helpVideoPath)
            }else{
                HelpPage(vm.helpVideoENPath)
            }
        }

        composable(LoginPageRoute.route) {
            LoginPage(vm = vm)
        }

        composable(AuditPageRoute.route) {
            AuditPage()
        }

        composable(ManagerPageRoute.route) {
            ManagerPage()
        }
        // Add more destinations similarly.

        composable(DebugPageRoute.route) {
            DebugPage()
        }


        composable(AdminPageRoute.route) {
            AdminPage(vm)
        }

        composable(DeviceParamPageRoute.route) {
            ParamPage()
        }

        composable(AvdParamPageRoute.route) {
            AdvParamPage()
        }
        // Add more destinations similarly.

        composable(CleanPageRoute.route) {
            CleanPage()
        }


    }

}


@Composable
fun SetLanguage(language: String) {
    val locale = Locale(language)
    val configuration = LocalConfiguration.current
    configuration.setLocale(locale)
    val resources = LocalContext.current.resources
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

@Throws(Exception::class)
private fun getVersion(context: Context): PackageInfo? {
    val packageManager = context.packageManager
    return packageManager.getPackageInfo(context.packageName, 0)
}
