package com.example.mealmate.ui.home.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mealmate.R
import com.example.mealmate.model.GroceryItem
import com.example.mealmate.viewmodel.GroceryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroceryFragment : BaseFragment() {
    private val viewModel: GroceryViewModel by viewModels()
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val groceryItems = mutableListOf<GroceryItem>()
    private lateinit var sendButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grocery, container, false)

        val etGroceryName = view.findViewById<EditText>(R.id.etGroceryName)
        val etGroceryQuantity = view.findViewById<EditText>(R.id.etGroceryQuantity)
        val btnAddGrocery = view.findViewById<Button>(R.id.btnAddGrocery)
        val seeGroceryList = view.findViewById<Button>(R.id.seeGroceryList)


        btnAddGrocery.setOnClickListener {
            val name = etGroceryName.text.toString().trim()
            val quantity = etGroceryQuantity.text.toString().trim()

            if (name.isNotEmpty() && quantity.isNotEmpty()) {
                viewModel.addGroceryItem(name, quantity)
                etGroceryName.text.clear()
                etGroceryQuantity.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter both name and quantity", Toast.LENGTH_SHORT).show()
            }
        }

        seeGroceryList.setOnClickListener {

            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(requireContext())

            // on below line we are inflating a layout file which we have created.
            val viewBottomSheet = layoutInflater.inflate(R.layout.add_grocery_bottom_sheet, null)
            val lstView = viewBottomSheet.findViewById<ListView>(R.id.lvGroceryItems)
            val sendGroceryListButton = viewBottomSheet.findViewById<ImageView>(R.id.shareBtn)



            adapter = ArrayAdapter(requireContext(), R.layout.list_item, mutableListOf())
            lstView.adapter = adapter

            viewModel.groceryItems.observe(viewLifecycleOwner) { items ->
                groceryItems.clear()
                groceryItems.addAll(items)
                adapter.clear()
                adapter.addAll(
                    groceryItems.map {
                        val status = if (it.purchased) "(✔ \uD835\uDE17\uD835\uDE36\uD835\uDE33\uD835\uDE24\uD835\uDE29\uD835\uDE22\uD835\uDE34\uD835\uDE26\uD835\uDE25)" else ""
                        "${it.name} - ${it.quantity} $status"
                    }
                )
                adapter.notifyDataSetChanged()
            }

            lstView.setOnItemClickListener { _, clickedView, position, _ ->
                val selectedItem = groceryItems[position]
                showOptionsDialog(clickedView, selectedItem)
            }

            sendGroceryListButton.setOnClickListener {
                sendGroceryList()
            }

            dialog.setCancelable(true)


            dialog.setContentView(viewBottomSheet)
            dialog.show()
        }


        viewModel.loadGroceryItems()
        return view
    }

    private fun showOptionsDialog(view: View, item: GroceryItem) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.grocery_menu, popupMenu.menu)

        // Change menu item text based on purchase status
        val purchaseMenuItem = popupMenu.menu.findItem(R.id.menu_mark_purchased)
        purchaseMenuItem.title = if (item.purchased) "Unmark as Purchased" else "Mark as Purchased"

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> showEditDialog(item)
                R.id.menu_delete -> {
                    viewModel.deleteGroceryItem(item.id)
                    Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_mark_purchased -> {
                    viewModel.togglePurchasedStatus(item)
                    Toast.makeText(
                        requireContext(),
                        if (item.purchased) "Item unmarked as purchased" else "Item marked as purchased",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showEditDialog(item: GroceryItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_grocery, null)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditGroceryName)
        val etEditQuantity = dialogView.findViewById<EditText>(R.id.etEditGroceryQuantity)

        etEditName.setText(item.name)
        etEditQuantity.setText(item.quantity)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newName = etEditName.text.toString().trim()
                val newQuantity = etEditQuantity.text.toString().trim()

                if (newName.isNotEmpty() && newQuantity.isNotEmpty()) {
                    val updatedItem = item.copy(name = newName, quantity = newQuantity)
                    viewModel.updateGroceryItem(updatedItem)
                    Toast.makeText(requireContext(), "Item updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun sendGroceryList() {
        if (groceryItems.isEmpty()) {
            Toast.makeText(requireContext(), "Grocery list is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val groceryText = groceryItems.joinToString("\n") { "${it.name}: ${it.quantity}" }
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:")
            putExtra("sms_body", "Grocery List:\n$groceryText")
        }

        startActivity(smsIntent)
    }
}
