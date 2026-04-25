// core/utils/ViewExtensions.kt
package com.commicart.app.core.utils

import android.app.Activity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Generic view finder
inline fun <reified T : View> Activity.bindView(id: Int): Lazy<T> = lazy {
    findViewById<T>(id)
}

fun <T : View> Activity.find(id: Int): T {
    return findViewById(id)
}

// Text extraction extensions
fun Activity.getEditTextValue(id: Int): String {
    return findViewById<EditText>(id).text.toString().trim()
}

fun Activity.getTextViewValue(id: Int): String {
    return findViewById<TextView>(id).text.toString().trim()
}

// Setting text extensions
fun Activity.setText(id: Int, text: String) {
    findViewById<TextView>(id).text = text
}

fun Activity.setHint(id: Int, hint: String) {
    val view = findViewById<View>(id)
    when (view) {
        is TextView -> view.hint = hint
        is EditText -> view.hint = hint
    }
}

// NEW: Set text color extension
fun Activity.setTextColor(id: Int, color: Int) {
    findViewById<TextView>(id).setTextColor(color)
}

// Visibility extensions
fun Activity.show(id: Int) {
    findViewById<View>(id).visibility = View.VISIBLE
}

fun Activity.hide(id: Int) {
    findViewById<View>(id).visibility = View.GONE
}

fun Activity.invisible(id: Int) {
    findViewById<View>(id).visibility = View.INVISIBLE
}

// Enable/Disable extensions
fun Activity.enable(id: Int) {
    findViewById<View>(id).isEnabled = true
}

fun Activity.disable(id: Int) {
    findViewById<View>(id).isEnabled = false
}

// Click listener extensions
fun Activity.onClick(id: Int, action: () -> Unit) {
    findViewById<View>(id).setOnClickListener { action() }
}

fun Activity.onLongClick(id: Int, action: () -> Boolean) {
    findViewById<View>(id).setOnLongClickListener { action() }
}

// Toast extensions
fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// ProgressBar extensions
fun Activity.showProgress(id: Int) {
    findViewById<ProgressBar>(id).visibility = View.VISIBLE
}

fun Activity.hideProgress(id: Int) {
    findViewById<ProgressBar>(id).visibility = View.GONE
}

// RecyclerView extensions
fun Activity.setupRecyclerView(id: Int, orientation: Int = LinearLayoutManager.VERTICAL) {
    val recyclerView = findViewById<RecyclerView>(id)
    recyclerView.layoutManager = LinearLayoutManager(this, orientation, false)
}

fun Activity.setupGridRecyclerView(id: Int, spanCount: Int = 2) {
    val recyclerView = findViewById<RecyclerView>(id)
    recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
}

// Button extensions
fun Activity.setButtonText(id: Int, text: String) {
    findViewById<Button>(id).text = text
}

fun Activity.setButtonEnabled(id: Int, enabled: Boolean) {
    findViewById<Button>(id).isEnabled = enabled
}

// EditText extensions
fun Activity.clearEditText(id: Int) {
    findViewById<EditText>(id).text.clear()
}

fun Activity.setEditTextError(id: Int, error: String?) {
    findViewById<EditText>(id).error = error
}


// Alternative implementation
fun Activity.setCardBackgroundColor(id: Int, color: Int) {
    val cardView = findViewById<androidx.cardview.widget.CardView>(id)
    cardView.setCardBackgroundColor(color)
}

//fun Activity.setCardStroke(id: Int, width: Int, color: Int) {
//    val cardView = findViewById<androidx.cardview.widget.CardView>(id)
//    cardView.setStrokeWidth(width)
//    cardView.setStrokeColor(color)
//}
// Multiple views visibility
fun Activity.show(vararg ids: Int) {
    ids.forEach { show(it) }
}

fun Activity.hide(vararg ids: Int) {
    ids.forEach { hide(it) }
}

// Get view reference
fun <T : View> Activity.view(id: Int): T {
    return findViewById(id)
}