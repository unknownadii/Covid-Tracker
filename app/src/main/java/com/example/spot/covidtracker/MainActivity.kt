package com.example.spot.covidtracker


import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Window
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var adapter : StateAdapter
    lateinit var dialog:Dialog
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        //Setting the ToolBar
        val toolBarHome = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolBarHome)
        setSupportActionBar(toolBarHome)

        //getting data for India's Cases
        // Recycler View Working
       val  recyclerView = findViewById<RecyclerView>(R.id.rv_StatesCases)
        //Setting Layout Manager
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        //Adapter Calling
        adapter = StateAdapter(this)
        showDialog()
        getApiDataIndia()
        //Attaching Adapter
        recyclerView.adapter = adapter
        val serachView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewState)
        serachView.queryHint = "Enter State Name"

        serachView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun getApiDataIndia(){
        val urlApi = "https://api.rootnet.in/covid19-in/stats/latest"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, urlApi, null, {
                val data = it.getJSONObject("data")

                //India Data
                val summary = data.getJSONObject("summary")
                val totalCases = summary.getInt("total")
                val totalDeaths = summary.getInt("deaths")
                val totalRecovered = summary.getInt("discharged")

                //State Wise Data regional
                val regional = data.getJSONArray("regional")
                var list = ArrayList<StateDataModel>()

                for (i in 0 until regional.length()) {
                    val index = regional.getJSONObject(i)
                    val state = index.getString("loc")
                    val stateCases = index.getString("totalConfirmed")
                    list.add(StateDataModel(state, stateCases.toLong()))
                }
                //Indian Cases
                fillDataIndiaCases(
                    totalCases.toLong(),
                    totalDeaths.toLong(),
                    totalRecovered.toLong()
                )
                adapter.update(list)
            },
            {
                Toast.makeText(this, "India Data is not Available", Toast.LENGTH_SHORT).show()
            }
        )
        HomeSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun fillDataIndiaCases(cases: Long, deaths: Long, recovered: Long) {
          hideDialog()
        val tCases = findViewById<TextView>(R.id.indiaCases)
        val tDeaths = findViewById<TextView>(R.id.indiaDeaths)
        val tRecovered = findViewById<TextView>(R.id.indiaRecovered)
        tCases.text = cases.toString()
        tDeaths.text = deaths.toString()
        tRecovered.text = recovered.toString()
    }

    private fun showDialog()
    {
        dialog= Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun hideDialog()
    {
        dialog.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logout-> {
                auth.signOut()
                startActivity(Intent(this,AuthenticationActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}