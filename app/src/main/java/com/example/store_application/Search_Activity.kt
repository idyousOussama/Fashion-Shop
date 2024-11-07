package com.example.store_application

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application.Adapters.SearchItemsAdapter
import com.example.store_application.AppliactionObjs.Product
import com.example.store_application.Listeners.SearchItemListener
import com.example.store_application.databinding.ActivitySearchActivityBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class Search_Activity : AppCompatActivity() {
    lateinit var binding :ActivitySearchActivityBinding
var firebaseDB = FirebaseDatabase.getInstance()
    var startRefrech = false
    var itemProductAdapter = SearchItemsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
binding = ActivitySearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
initSerchBar()
initRefrech()
        setListener()
    initGoBackBtn()

    }

    private fun initGoBackBtn() {
        binding.searchGoBackBtn.setOnClickListener {
            finish()
        }
    }

    private fun setListener() {
        itemProductAdapter.setItemListener(object :SearchItemListener{
            override fun onItemClicked(product: Product) {
                var intent = Intent(baseContext , Product_Details_Activities::class.java)
                intent.putExtra("selectedProduct" , product )
            startActivity(intent)
            }

        })
    }

    private fun initRefrech() {
        binding.noResultText.setOnClickListener {
var searchText = binding.searchInupt.text.toString()
            if(startRefrech && !searchText.isEmpty()){
                getProuduct(searchText)
            }else if(searchText.isEmpty()){
                Toast.makeText(baseContext,R.string.add_text,Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun initSerchBar() {
        binding.searchInupt.addTextChangedListener { TextWatcher ->
            var searchText = binding.searchInupt.text.toString()
            if(!searchText.isEmpty()){
                initProgress(1)
                getProuduct(searchText)
            }else{
                binding.searchProductList.visibility= View.GONE
                binding.noResutLayout.visibility= View.GONE
            }
        }
    }

    private fun initProgress(i: Int) {
        binding.searchProductList.visibility= View.VISIBLE
        if (i==1){
            binding.searchProductList.visibility = View.GONE
            binding.noResutLayout.visibility = View.GONE
            binding.searchProductProgress.visibility = View.VISIBLE
        }else if (i == 2){
            binding.noResutLayout.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.searchProductList.visibility = View.VISIBLE
        }else if (i == 0){
            binding.searchProductList.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.searchList.setImageResource(R.drawable.refrech)
            binding.noResultText.setBackgroundResource(R.drawable.add_carts_btn_backround)
            binding.noResultText.setText(R.string.refrech_text)
            binding.noResutLayout.visibility = View.VISIBLE
            startRefrech = true
        }
        else{
            binding.searchProductList.visibility = View.GONE
            binding.searchProductProgress.visibility = View.GONE
            binding.noResutLayout.visibility = View.VISIBLE
        }

    }
    private fun getProuduct(searchText: String) {
        val productRef = firebaseDB.getReference("Products")
       var productList:ArrayList<Product> = ArrayList()
        productRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var product = item.getValue(Product::class.java)
                        if(product != null){
                            if (product.productName.contains(searchText)){
                                productList.add(product)
                            }
                        }
                    }
                    if (!productList.isEmpty()){
                        initProgress(2)
                        setList(productList)
                    }else{
                        initProgress(3)
                    }

                }else{
                    initProgress(3)                }
            }

            override fun onCancelled(p0: DatabaseError) {
         initProgress(0)
            }

        })


    }

    private fun setList(productList: ArrayList<Product>) {
        itemProductAdapter.setSearchList(productList)
        binding.searchProductList.layoutManager = GridLayoutManager(this,1)
            binding.searchProductList.setHasFixedSize(true)
        binding.searchProductList.adapter = itemProductAdapter


    }


}