import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.adapter.MealPlanAdapter
import com.example.mealmate.utils.RecipeSelectionDialog
import com.example.mealmate.viewmodel.MealPlanViewModel
import com.example.mealmate.viewmodel.RecipeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DayMealFragment : Fragment() {
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var addMealButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealPlanAdapter
    private var day: String? = null
    private val mealPlanViewModel: MealPlanViewModel by viewModels()


    companion object {
        private const val ARG_DAY = "arg_day"

        fun newInstance(day: String): DayMealFragment {
            return DayMealFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DAY, day)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        day = arguments?.getString(ARG_DAY) // Get day from arguments
        day?.let { mealPlanViewModel.loadMealPlans(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_meal_day, container, false)

        addMealButton = view.findViewById(R.id.addRecipeButtonMealPlan)
        recyclerView = view.findViewById(R.id.recyclerViewMealPlan)

        mealAdapter = MealPlanAdapter(emptyList(),mealPlanViewModel)
        recyclerView.adapter = mealAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        mealPlanViewModel.mealPlans.observe(viewLifecycleOwner, { mealPlans ->
            val recipes = mealPlans.map { it }
            mealAdapter.updateData(recipes)
        })


        addMealButton.setOnClickListener {
            day?.let { selectedDay ->
                viewModel.loadRecipes()
                RecipeSelectionDialog(viewModel, selectedDay).show(childFragmentManager, "RecipeSelectionDialog")
            }
        }


        return view
    }
}
