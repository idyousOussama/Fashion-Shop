package com.example.store_application.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.example.store_application.AppliactionObjs.Bunner
import com.example.store_application.R
import com.squareup.picasso.Picasso

class SliderBunnerAdaptetr(var bunnersList : ArrayList<Bunner>, val viewPAger : ViewPager2) : RecyclerView.Adapter<SliderBunnerAdaptetr.BunnerViewHolder> () {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BunnerViewHolder {
val view =LayoutInflater.from(p0.context).inflate(R.layout.bunner_custom, p0 , false)
        return BunnerViewHolder(view)
    }

    override fun getItemCount(): Int {
return  bunnersList.size
    }

    override fun onBindViewHolder(p0: BunnerViewHolder, p1: Int) {
        var bunner : Bunner= bunnersList.get(p1)
        p0.setImage(bunner.bunnerImage)

        if(p1 == bunnersList.lastIndex - 1){
            viewPAger.post(runnable)
        }
     }
    class BunnerViewHolder(itemView: View) : ViewHolder(itemView) {
 private val bunnerPic  = itemView.findViewById<ImageView>(R.id.bunner_image)

        fun setImage(bunnerImg : String){
            Picasso.get().load(bunnerImg.toUri()).placeholder(R.drawable.place_holder_image).into(bunnerPic)
        }
    }
    private val runnable = Runnable {
        bunnersList = bunnersList

    }
}