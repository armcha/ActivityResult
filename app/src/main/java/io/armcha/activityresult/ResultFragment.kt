package io.armcha.activityresult

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

class ResultFragment : Fragment() {

    companion object {

        fun newInstance(vararg params: Pair<String, Any>): ResultFragment {
            val resultFragment = ResultFragment()
            resultFragment.arguments = bundleOf(*params)
            return resultFragment
        }
    }

    var callback: (ActivityResult) -> Unit = {}
    var clazz: Class<*>? = null

    init {
        retainInstance = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val activityResult = ActivityResult(resultCode, data)
        callback(activityResult)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .remove(this)
            .commitNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(activity, clazz)
        arguments?.let {
            intent.putExtras(it)
        }
        startActivityForResult(intent, 123)
    }
}

inline fun <reified C> AppCompatActivity.startActivityForResult(
    vararg params: Pair<String, Any>,
    noinline callback: (ActivityResult) -> Unit) {

    val resultFragment = ResultFragment.newInstance(*params)
    resultFragment.clazz = C::class.java
    resultFragment.callback = callback

    supportFragmentManager.beginTransaction()
        .add(resultFragment, "${resultFragment.hashCode()}")
        .commit()
}