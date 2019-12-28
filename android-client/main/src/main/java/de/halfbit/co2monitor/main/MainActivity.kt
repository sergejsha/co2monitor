package de.halfbit.co2monitor.main

import android.os.Bundle
import de.halfbit.co2monitor.main.epi.MainFragmentFactory
import de.halfbit.co2monitor.main.magnet.MagnetActivity
import magnet.getSingle

internal class MainActivity : MagnetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMainFragment(savedInstanceState)
    }

    private fun createMainFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.mainContainer, scope.getSingle<MainFragmentFactory>().create())
                .commit()
    }
}
