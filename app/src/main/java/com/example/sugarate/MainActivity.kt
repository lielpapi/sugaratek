package com.example.sugarate

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sugarate.databinding.ActivityMainBinding // Assuming you are using View Binding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            fetchRandomRecipe()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun fetchRandomRecipe() {
        val request = Request.Builder()
            .url("https://www.themealdb.com/api/json/v1/1/random.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Failed to fetch recipe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                responseData?.let {
                    try {
                        val jsonObject = JSONObject(it)
                        val mealsArray = jsonObject.getJSONArray("meals")
                        if (mealsArray.length() > 0) {
                            val mealObject = mealsArray.getJSONObject(0)
                            val recipeName = mealObject.getString("strMeal")
                            val recipeInstructions = mealObject.getString("strInstructions")
                            runOnUiThread {
                                showRecipeDialog(recipeName, recipeInstructions)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "No recipe found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun showRecipeDialog(recipeName: String, recipeInstructions: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(recipeName)
        builder.setMessage(recipeInstructions)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}