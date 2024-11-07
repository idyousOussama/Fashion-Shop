package com.example.store_application

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.store_application.Adapters.CategoriesAdapter
import com.example.store_application.Adapters.ProductAdapter
import com.example.store_application.Adapters.SliderBunnerAdaptetr
import com.example.store_application.Adapters.UserAccountAdapter
import com.example.store_application.AppliactionObjs.Accounts
import com.example.store_application.AppliactionObjs.Bunner
import com.example.store_application.AppliactionObjs.Category
import com.example.store_application.AppliactionObjs.Conversation
import com.example.store_application.AppliactionObjs.Message
import com.example.store_application.AppliactionObjs.Product
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.Listeners.CategoryItemListener
import com.example.store_application.Listeners.ProductItemListener
import com.example.store_application.Listeners.UserAccountListener
import com.example.store_application.RoomDatabase.StoreViewModele
import com.example.store_application.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
 private lateinit  var binding : ActivityMainBinding
    private var productAdapter = ProductAdapter()
    var currentUser: User?= null
    private var categoriyAdapter = CategoriesAdapter()
var firebaseDB : FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var imagesAdapter:SliderBunnerAdaptetr
    lateinit var imagesList : ArrayList<Int>
    val accountsAdapter = UserAccountAdapter()
    var support :User?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBunners()
        navigationButtom()
        getCategories()
        getProducts()
        allBtnClicked()
        getCategoryItemListener()
        getProductItemListener()
        getCurrentUser()
        getSupport()
        showsheetDialog()
        userAccountItemClikced()
        checkNewMessages()
    }



    private fun userAccountItemClikced() {
accountsAdapter.setUserAccountItemListener(object : UserAccountListener{
    override fun UserAccountItemClicked(email: String) {
         if(email != null ){
             val intent = Intent(baseContext,SingIn_Activity::class.java)
             intent.putExtra("logainedAccount" , email)
             startActivity(intent)
         }
    }

})
    }

    private fun showsheetDialog() {
binding.moreBtn.setOnClickListener {
    val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_custom,null)
    val bottomSheet = BottomSheetDialog(this,R.style.BottomSheetDialog)
    val accountList = bottomSheetView.findViewById<RecyclerView>(R.id.account_list)
    val createAccountBtn = bottomSheetView.findViewById<TextView>(R.id.create_newAccount_Btn)
    val addAccountBtn = bottomSheetView.findViewById<TextView>(R.id.add_newAccount_Btn)

    createNewAccount(createAccountBtn)
addNewAccount(addAccountBtn)
    bottomSheet.setContentView(bottomSheetView)
    getUserAccounts(accountList)
    bottomSheet.show()
}
    }

    private fun addNewAccount(addAccountBtn: TextView?) {
        addAccountBtn!!.setOnClickListener{
            goToNewActivity(SingIn_Activity::class.java)
        }
    }

    private fun createNewAccount(createAccountBtn: TextView?) {
        createAccountBtn?.setOnClickListener {
            goToNewActivity(Register_Activity::class.java)
        }

    }

    private fun goToNewActivity(newActivity: Class<*>) {
val intent = Intent(baseContext,newActivity)
        startActivity(intent)
    }

    private fun getUserAccounts(accountListRV : RecyclerView) {
        val storeViewModel = ViewModelProvider(this).get(StoreViewModele::class)
        CoroutineScope(Dispatchers.Main).launch{
            storeViewModel.getAllAccounts().observe(this@MainActivity,object : Observer<List<Accounts>>{
                override fun onChanged(value: List<Accounts>) {
                    if(!value.isEmpty()){
                        setAccountsList(value,accountListRV)
                    }else{

                    }
                }

            })
        }

    }

    private fun setAccountsList(value: List<Accounts>, accountListRV: RecyclerView) {
        accountsAdapter.setUserAccoutsLis(value)
        accountListRV.layoutManager = LinearLayoutManager(this)
        accountListRV.setHasFixedSize(true)
        accountListRV.adapter = accountsAdapter

    }

    private fun navigationButtom() {
            goToMessanger()
            goToSearch()
        goToCards()

    }

    private fun goToCards() {
        binding.cardBtn.setOnClickListener {
            val intent = Intent(baseContext,Cards_Activity::class.java)
            intent.putExtra("currentUserCard",currentUser)
            startActivity(intent)
        }
    }

    private fun goToSearch() {
        binding.searhBtn.setOnClickListener {
            val intent = Intent(baseContext,Search_Activity::class.java)
            startActivity(intent)
        }
        binding.searchBarCard.setOnClickListener{
            val intent = Intent(baseContext,Search_Activity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMessanger() {
        binding.supportMessanger.setOnClickListener {
            val intent = Intent(baseContext,Support_Messanger::class.java)
            intent.putExtra("mainRceiver",support)
            intent.putExtra("mainSender",currentUser)
            startActivity(intent)
        }
    }

    private fun getSupport() {
        val intent = intent
        support = intent.getSerializableExtra("SupportsUser") as? User
        Toast.makeText(baseContext,support!!.userId,Toast.LENGTH_SHORT).show()

    }
    private fun getCurrentUser() {
        val currentUserIntent = intent
         currentUser = currentUserIntent.getSerializableExtra("currentUser") as? User
Picasso.get().load(currentUser!!.userAccount!!.accountProfile).placeholder(R.drawable.user_place_holder).into(binding.userPic)
        binding.userName.setText("Hello," +currentUser!!.userAccount!!.accountName)

    }

    private fun getProductItemListener() {
        productAdapter.setItemLestener(object : ProductItemListener{
                override fun onItemClicked(product: Product) {
var productIntent = Intent (baseContext,Product_Details_Activities::class.java)
                    productIntent.putExtra("selectedProduct" ,product)
                    productIntent.putExtra("selectedUser",currentUser)
                    startActivity(productIntent)
                }

            override fun onLikedProductListner(product: Product) {
                val productRef = firebaseDB.getReference("Products")
                   val proLikers = firebaseDB.getReference("ProductLikers").child(product.productId)

                proLikers.child(currentUser!!.userId).setValue(currentUser).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        upDateProductLikes(product,productRef)
                    }
                }
            }
        })
    }

    private fun upDateProductLikes(product: Product, productRef: DatabaseReference) {
        var upDates = mapOf<String,Int>(
            "likesNum" to product.likesNum + 1
        )
        productRef.child(product.productId).updateChildren(upDates)
    }

    private fun getCategoryItemListener() {
        categoriyAdapter.setListener(object : CategoryItemListener{
            override fun onItemViewClicked(lastIteViewText: TextView, category: Category) {
                lastIteViewText.setTextColor(resources.getColor(R.color.black))
                binding.allText.setTextColor(resources.getColor(R.color.black))
               onChnageChnageCtaegoryProduct()
                getCategoriesProduct(category.categoryName)
                binding.productCategorieName.setText(category.categoryName + " Products")
            }

        })    }

    private fun onChnageChnageCtaegoryProduct() {
        binding.productProgress.visibility = View.VISIBLE
        binding.productList.visibility = View.GONE
        binding.noCategorySteker.visibility = View.GONE }
    private fun getCategoriesProduct(categoryName: String) {
var catProRef = firebaseDB.getReference("Products")
        var catProList :ArrayList<Product> = ArrayList()
        catProRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var product = item.getValue(Product::class.java)
                        if (product != null) {
                            if (product.productName == categoryName){
                                catProList.add(product)
                            }
                        }
                    }
                    if(!catProList.isEmpty()){
                        produtsExist(true)
                        productAdapter.setProductList(catProList,currentUser!!)
                    }else{
                        produtsExist(false)
                    }

                 }else{
                    produtsExist(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                produtsExist(false)
            }

        })
    }
    private fun allBtnClicked() {
binding.allText.setOnClickListener {
    onChnageChnageCtaegoryProduct()
    if(categoriyAdapter.lastItemViewTxet != null){
        categoriyAdapter.currentItem!!.setTextColor(resources.getColor(R.color.black))
        binding.allText.setTextColor(resources.getColor(R.color.brow))
        binding.productCategorieName.setText(R.string.All_Text)
        getProducts()
    }else{
    }
}
    }
    private fun getProducts() {
        var productRef = firebaseDB.getReference("Products")
        var productList :ArrayList<Product> = ArrayList()
        productRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
for(item in p0.children){
    var product = item.getValue(Product::class.java)
    if (product != null) {
        productList.add(product)
    }
}
                    if (productList.size > 0){
                        produtsExist(true)
                        setProducts(productList )
                    }else{
                        produtsExist(false)
                    }
                }else{
                    produtsExist(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                produtsExist(false)
            }

        })
    }
    private fun produtsExist(isExist: Boolean) {
if(isExist){
    binding.productProgress.visibility = View.GONE
    binding.productList.visibility = View.VISIBLE
    binding.noProductSteker.visibility = View.GONE
}else{
    binding.productProgress.visibility = View.GONE
    binding.productList.visibility = View.GONE
    binding.noProductSteker.visibility = View.VISIBLE
}
    }
    private fun setProducts(productList: ArrayList<Product>) {
      productAdapter.productsList = productList
        productAdapter.currentUsr = currentUser
        binding.productList.layoutManager = GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        binding.productList.setHasFixedSize(true)
        binding.productList.adapter = productAdapter

    }
    private fun getCategories() {
        var categoriesRef = firebaseDB.getReference("Categories")
        var categoriesList :ArrayList<Category> = ArrayList()
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(item in p0.children){
                        var category = item.getValue(Category::class.java)
                        if (category != null) {
                            categoriesList.add(category)
                        }
                    }
                    if(categoriesList.size > 0){
                        categoriesExists(true)
                       setCategories(categoriesList)
                    }else{
                        categoriesExists(false)

                    }
                }else{
                    categoriesExists(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun categoriesExists(cateExists: Boolean) {
if (cateExists){
    binding.categoryProgress.visibility = View.GONE
    binding.noCategorySteker.visibility = View.GONE
    binding.categoriesList.visibility = View.VISIBLE
    binding.allText.visibility = View.VISIBLE
}else{
    binding.categoryProgress.visibility = View.GONE
    binding.noCategorySteker.visibility = View.VISIBLE
    binding.categoriesList.visibility = View.GONE
    binding.allText.visibility = View.GONE
}
    }

    private fun setCategories(categoriesList: ArrayList<Category>) {
        categoriyAdapter.categoryList = categoriesList
        binding.categoriesList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.categoriesList.setHasFixedSize(true)
        binding.categoriesList.adapter = categoriyAdapter
    }

    private fun getBunners() {
        val bunnersRef = firebaseDB.getReference("Bunners")
        var bunnersList : ArrayList<Bunner> = ArrayList()
        bunnersRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    hideAndUnhideBunner(2)
                    for (item in p0.children){
                        var bunner = item.getValue(Bunner::class.java)
                        if (bunner != null) {
                            bunnersList.add(bunner)
                        }
                    }
                    if(bunnersList.size > 0){
                        hideAndUnhideBunner(2)
                        initBunner(bunnersList)
                    }else{
                        hideAndUnhideBunner(1)
                    }
                }else{
                    hideAndUnhideBunner(1)
                }
            }



            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun hideAndUnhideBunner(i: Int) {
if(i == 1){
    binding.bunnerContainer.visibility = View.GONE
binding.bunnerDotsIndecator.visibility = View.GONE
}else{
    binding.bunnerContainer.visibility = View.VISIBLE
    binding.bunnerDotsIndecator.visibility = View.VISIBLE
}
    }
    private fun initBunner(bunnersList : ArrayList<Bunner>) {
if(bunnersList.size == 1){
    binding
        .bunnerDotsIndecator.visibility = View.GONE
}
        imagesAdapter = SliderBunnerAdaptetr(bunnersList,binding.viewPager)
        binding.viewPager.adapter =imagesAdapter
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.clipToPadding =   false
        binding.viewPager.clipChildren = false
        binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
val cpt =CompositePageTransformer().apply {
    addTransformer(MarginPageTransformer(40))
}
   binding.viewPager.setPageTransformer(cpt)
        binding.bunnerDotsIndecator.attachTo(binding.viewPager)
    }

    private fun checkNewMessages() {
        var newMesageList: ArrayList<Message> = ArrayList()
        var conversationMessageRef = firebaseDB.getReference("Rooms").child( currentUser!!.userId + support!!.userId).child("NewMessages")
        conversationMessageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                        for(item in p0.children){
                            var newMessage = item.getValue(Message::class.java)
                            if(newMessage != null){
                                newMesageList.add(newMessage)
                            }
                        }
                    if(newMesageList.isNotEmpty()){
                        binding.mainNewMessage.visibility = View.VISIBLE
                    }else{
                        binding.mainNewMessage.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}