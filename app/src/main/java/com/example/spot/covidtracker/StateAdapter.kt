package com.example.spot.covidtracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class StateAdapter(var context: Context) : RecyclerView.Adapter<StateAdapter.ViewHolder>(),
    Filterable {

    private val list: ArrayList<StateDataModel> = ArrayList()

    var countryFilterList = ArrayList<StateDataModel>()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val state = view.findViewById<TextView>(R.id.state)
        val stateCases = view.findViewById<TextView>(R.id.stateCases)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_states_corona, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewPostion: StateDataModel = list[position]
        holder.state.text = viewPostion.state
        holder.stateCases.text = viewPostion.totalCases.toString()
    }

    fun update(updatedItems: ArrayList<StateDataModel>) {
        countryFilterList.clear()
        list.clear()
        countryFilterList.addAll(updatedItems)
        list.addAll(updatedItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredStateList = ArrayList<StateDataModel>()
                if(constraint ==null || constraint.length == 0)
                {
                    filteredStateList.addAll(countryFilterList)
                }
                else
                {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (row in countryFilterList)
                    {
                        if (row.state.toLowerCase().contains(filterPattern))
                        {
                            filteredStateList.add(row)
                        }
                    }
                }
                val result = FilterResults()
                result.values =  filteredStateList
                result.count = filteredStateList.size
                return result
            }
            override fun publishResults(constraint: CharSequence?, result: FilterResults?) {
                //clearing the previous data of recycler view
                list.clear()
                if (result != null) {
                    // adding new data as per search content
                    list.addAll(result.values as ArrayList<StateDataModel>)
                }
                notifyDataSetChanged()
            }
        }
    }
}