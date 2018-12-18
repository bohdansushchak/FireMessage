package com.sushchak.bohdan.firemessage.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.sushchak.bohdan.firemessage.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(PeopleFragment())
        supportActionBar?.title = "Contacts"
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_people -> {

                replaceFragment(PeopleFragment())
                supportActionBar?.title = "Contacts"
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_my_account -> {

                replaceFragment(MyAccountFragment())
                supportActionBar?.title = "Account"
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment)
            .commit()
    }
}
