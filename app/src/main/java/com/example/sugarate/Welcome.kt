package com.example.sugarate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sugarate.login.LoginActivity
import com.example.sugarate.signup.SignUpActivity
import com.example.sugarate.utils.Utils
import com.google.firebase.auth.FirebaseAuth


class Welcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Check if a user is already logged in using Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is already logged in, navigate to MainActivity
            navigateToMainActivity()
            return // Finish the Welcome activity to prevent it from being shown
        }

        // initialize the database with a new Recipe ff

        Log.d("RECIPE", "Before initializing database with recipe")

        Utils.initializeDatabaseWithRecipe(application, "dish_photos/lasagna.jpg", "Lasagna", "Lasagna noodles, ground beef, onion, garlic, crushed tomatoes, tomato paste, dried basil, dried oregano, salt, pepper, ricotta cheese, egg, mozzarella cheese, Parmesan cheese, fresh basil leaves ", "Preheat oven. Cook noodles. Brown beef with onion and garlic. Stir in tomatoes, paste, basil, oregano, salt, and pepper. Mix ricotta with egg. Layer ingredients in baking dish. Bake covered, then uncovered until golden. Let cool before serving. Garnish with basil leaves.", 750)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/meatball.jpeg", "Meatball", "Ground beef, breadcrumbs, milk, Parmesan cheese, egg, garlic, dried oregano, dried basil, salt, pepper, olive oil, marinara sauce ", "Preheat oven. Combine ingredients in a bowl. Shape into balls. Place on baking sheet. Drizzle with olive oil. Bake until browned. Serve with marinara sauce.", 600)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/pizza.jpeg", "Pizza", "Pizza dough, pizza sauce, mozzarella cheese, toppings (e.g., pepperoni, mushrooms, bell peppers, onions, olives)", "Preheat oven. Roll out dough. Spread sauce. Sprinkle cheese. Add toppings. Bake until crust is golden and cheese is bubbly.", 900)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/hamburger.jpg", "Hamburger", "Ground beef, hamburger buns, lettuce, tomato, onion, cheese slices, condiments (e.g., ketchup, mustard, mayonnaise) ", "Form beef into patties. Season with salt and pepper. Cook on grill or stovetop. Toast buns. Assemble burgers with toppings and condiments.", 690)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/bakedFish.jpg", "Baked Fish", "Fish fillets (e.g., cod, salmon, tilapia), lemon, olive oil, salt, pepper, herbs (e.g., dill, parsley), garlic (optional)", "Preheat oven. Place fish on baking sheet. Drizzle with olive oil. Season with salt, pepper, and herbs. Add lemon slices. Bake until fish flakes easily.",420)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/spaghetti_carbonara.jpg", "Spaghetti Carbonara", "Spaghetti, pancetta or bacon, eggs, Parmesan cheese, black pepper, garlic (optional)", "Cook spaghetti. In a separate pan, cook pancetta or bacon until crispy. Mix eggs, Parmesan cheese, and black pepper in a bowl. Drain spaghetti and add to the pan with pancetta. Remove from heat and quickly stir in egg mixture until creamy. Serve immediately.", 550)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/chicken_tikka_masala.jpeg", "Chicken Tikka Masala", "Chicken breast, yogurt, lemon juice, garlic, ginger, garam masala, cumin, paprika, turmeric, tomato sauce, cream, butter, cilantro", "Marinate chicken in yogurt, lemon juice, and spices. Grill or bake chicken until cooked. In a separate pan, sauté garlic and ginger, then add tomato sauce and cream. Simmer until thickened. Add cooked chicken to the sauce and simmer for a few more minutes. Garnish with cilantro and serve with rice or naan.", 700)
        Utils.initializeDatabaseWithRecipe(application, "dish_photos/caesar_salad.jpg", "Caesar Salad", "Romaine lettuce, Caesar dressing, Parmesan cheese, croutons, lemon juice, olive oil, garlic, anchovy paste (optional)", "Toss chopped romaine lettuce with Caesar dressing, Parmesan cheese, and croutons. Make dressing by whisking together lemon juice, olive oil, garlic, and anchovy paste. Serve chilled as a side or with grilled chicken for a main course.", 350)


        Log.d("RECIPE", "After initializing database with recipe")


        val registerButton: Button = findViewById(R.id.create_account_button)
        val loginButton: Button = findViewById(R.id.login_button)

        registerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish() // Finish the Welcome activity to make it disappear
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish the Welcome activity to make it disappear
        }

    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
