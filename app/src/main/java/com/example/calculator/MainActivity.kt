package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var canAddOperation = false
    var canAddDecimal = true
    var regex: Regex = Regex("(^[0-9?x?/?+?-]+(\\.[0-9?x?/?+?-]{0,999})?)?")
    var workingTextValue: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.workingsText.addTextChangedListener(object : TextWatcher
        {
            var lastStringState = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    Log.d("TextWatcher", "${regex.matches(it)}")
                    if (lastStringState != it.toString()) {
                        if (regex.matches(it)) {
                            lastStringState = s.toString()
                            binding.workingsText.text = it
                        } else {
                            binding.workingsText.text = lastStringState
                        }
                    }
                }
            }
        })


    }

    fun numberButton(view: View) {

        if(view is Button) {
            binding.workingsText.append(view.text)
            canAddOperation = true
        }


    }
    fun operatorButton(view: View) {

        if(view is Button && canAddOperation) {
            binding.workingsText.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }

    }
    fun backSpace(view: View) {
        val length = binding.workingsText.length()

        if (length > 0 ) {
            binding.workingsText.text = binding.workingsText.text.subSequence(0, length -1)
        }
    }
    fun allClear(view: View) {
        binding.workingsText.text = ""
        binding.resultText.text = ""
    }
    fun equalsButton(view: View) {

        binding.resultText.text = calculateResults()
    }

    private fun calculateResults(): String {

        val digitsOperators = digitsOperator()
        if (digitsOperators.isEmpty()) {

            return ""
        }

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) {

            return ""
        }
        val result = addSubtractCalculate(timesDivision)
        return result.toString()

    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {

        var result = passedList[0] as Float

        for (i in passedList.indices) {

            if (passedList[i] is Char && i != passedList.lastIndex ) {

                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float

                if (operator == '+') {
                    result += nextDigit
                }
                if (operator == '-') {
                    result -= nextDigit
                }
            }
        }

        return result

    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {

        var list = passedList

        while (list.contains('x') || list.contains('/')) {

            list = calculateTimeDivision(list)

        }
        return list
    }



    private fun calculateTimeDivision(passedList: MutableList<Any>): MutableList<Any> {

        val newList = mutableListOf<Any>()

        var restartIndex = passedList.size

        for (i in passedList.indices) {

            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {

                val operator = passedList [i]
                val prevDigit = passedList [i - 1] as Float
                val nextDigit = passedList [i + 1] as Float

                when (operator) {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if (i > restartIndex)
            {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    fun digitsOperator(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""

        for (character in binding.workingsText.text) {

            if (character.isDigit() || character == '.'){

                currentDigit += character
            } else {

                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }

        }

        if (currentDigit != "")

            list.add(currentDigit.toFloat())

        return list
    }


}