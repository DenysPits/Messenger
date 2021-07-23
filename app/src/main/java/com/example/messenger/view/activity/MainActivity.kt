package com.example.messenger.view.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.messenger.R
import com.example.messenger.view.fragment.ChatPreviewsFragmentDirections


class MainActivity : AppCompatActivity() {

    private lateinit var themeModeHandler: ThemeModeHandler
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        themeModeHandler = ThemeModeHandler(getPreferences(Context.MODE_PRIVATE)!!)
        themeModeHandler.restoreThemeMode()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setSupportActionBar(findViewById(R.id.toolbar))
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.registrationFragment,
                R.id.chatPreviewsFragment
            )
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.change_theme -> {
            if (isDarkThemeOn()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                themeModeHandler.savePreference(ThemeMode.LIGHT)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                themeModeHandler.savePreference(ThemeMode.DARK)
            }
            true
        }
        R.id.edit_profile -> {
            val action =
                ChatPreviewsFragmentDirections.actionChatPreviewsFragmentToProfileFragment()
            navController.navigate(action)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}