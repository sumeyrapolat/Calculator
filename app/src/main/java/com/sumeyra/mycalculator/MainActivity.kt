package com.sumeyra.mycalculator

import android.health.connect.datatypes.units.Percentage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.sumeyra.mycalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var inputValueOne: Double ?= 0.0
    private var inputValueTwo : Double ?= null
    private var currentOperator : Operator?= null
    private var result: Double ?= null
    private val equation : StringBuilder = java.lang.StringBuilder().append(ZERO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        setNightModeIndicator()
    }

    private fun setListener(){
        for (button in getNumericButtons()){
            button.setOnClickListener { onNumberClicked( button.text.toString()) }
        }
        with(binding){
            buttonZero.setOnClickListener { onZeroClicked() }
            buttonDoubleZero.setOnClickListener { onDoubleZeroClicked() }
            buttonDecimal.setOnClickListener { onDecimalPointClicked() }
            buttonAddition.setOnClickListener { onOperatorClicked(Operator.ADDITION) }
            buttonSubtraction.setOnClickListener { onOperatorClicked(Operator.SUBTRACTION) }
            buttonDivision.setOnClickListener { onOperatorClicked(Operator.DIVISION) }
            buttonMultiplication.setOnClickListener { onOperatorClicked(Operator.MULTIPLICATION) }
            buttonEquation.setOnClickListener { onEqualsClicked() }
            buttonAllClear.setOnClickListener { onAllClearClicked() }
            buttonPlusMinus.setOnClickListener { onPlusMinusClicked() }
            buttonPercentage.setOnClickListener { onPercentageClicked() }
            imageNightMode.setOnClickListener { toggleNightMode() }
        }
    }

    private fun toggleNightMode(){
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }

    private fun setNightModeIndicator(){
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.imageNightMode.setImageResource(R.drawable.contrast)
        }else{
            binding.imageNightMode.setImageResource(R.drawable.half_moon)
        }
    }

    private fun onPercentageClicked(){
        if (inputValueTwo == null){
            val percentage = getInputValueOne() / 100
            inputValueOne = percentage
            equation.clear().append(percentage)
            updateInputOnDisplay()
        }else{
            val percentageOfValueOne = (getInputValueOne()*getInputValueTwo()) /100
            val percentageOfValueTwo = getInputValueTwo() / 100

            result=when(requireNotNull(currentOperator)){
                Operator.ADDITION -> getInputValueOne() + percentageOfValueOne
                Operator.SUBTRACTION     -> getInputValueOne() - percentageOfValueOne
                Operator.MULTIPLICATION -> getInputValueOne() * percentageOfValueTwo
                Operator.DIVISION -> getInputValueOne() / percentageOfValueTwo
            }

            equation.clear().append(ZERO)
            updateResultOnDisplay(isPercentage = true)
            inputValueOne = result
            result = null
            inputValueTwo = null
            currentOperator = null

        }
    }

    private fun onPlusMinusClicked(){
        if(equation.startsWith(MINUS)){
            equation.deleteCharAt(0)
        }else{
            equation.insert(0, MINUS)
        }
        setInput()
        updateInputOnDisplay()
    }

    private fun onAllClearClicked(){
        inputValueOne = 0.0
        inputValueTwo = null
        currentOperator = null
        result = null
        equation.clear().append(ZERO)
        clearDisplay()
    }

    private fun onOperatorClicked(operator: Operator){
        onEqualsClicked()
        currentOperator = operator

    }

    private fun onEqualsClicked(){
        if (inputValueTwo != null){
            result= calculate()
            equation.clear().append(ZERO)
            updateResultOnDisplay()
            inputValueOne=result
            result = null
            inputValueTwo = null
            currentOperator = null
        }else{
            equation.clear().append(ZERO)
        }
    }


    private fun calculate(): Double{
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION -> getInputValueOne() + getInputValueTwo()
            Operator.SUBTRACTION -> getInputValueOne() - getInputValueTwo()
            Operator.MULTIPLICATION  -> getInputValueOne() * getInputValueTwo()
            Operator.DIVISION -> getInputValueOne() / getInputValueTwo()

        }

    }

    private fun onDecimalPointClicked(){
        if (equation.contains(DECIMAL_POINT)) return
        equation.append(DECIMAL_POINT)
        setInput()
        updateInputOnDisplay()
    }

    private fun onZeroClicked(){
        if (equation.startsWith(ZERO)) return
        onNumberClicked(ZERO)
    }

    private fun onDoubleZeroClicked(){
        if (equation.startsWith(ZERO)) return
        onNumberClicked(DOUBLE_ZERO)
    }

    private fun getNumericButtons() = with(binding) {
        arrayOf(
            buttonOne,
            buttonTwo,
            buttonThree,
            buttonFour,
            buttonFive,
            buttonSix,
            buttonSeven,
            buttonEight,
            buttonNine
        )
    }


    private fun onNumberClicked(numberText : String){
        if (equation.startsWith(ZERO)){
            equation.deleteCharAt(0)
        }else if (equation.startsWith("${ZERO}${ MINUS}")){
            equation.deleteCharAt(1)
        }
        equation.append(numberText)
        setInput()
        updateInputOnDisplay()
    }


    private fun setInput(){
        if (currentOperator == null){
            inputValueOne = equation.toString().toDouble()
        }else{
            inputValueTwo = equation.toString().toDouble()
        }
    }

    private fun clearDisplay(){
        with(binding){
            textInput.text = getFormattedDisplayValue(value = getInputValueOne())
            textEquation.text = null
        }
    }

    private fun updateResultOnDisplay(isPercentage: Boolean =false){
        binding.textInput.text = getFormattedDisplayValue(value = result)
        var input2Text = getFormattedDisplayValue(value = getInputValueTwo())
        if (isPercentage) input2Text = "${input2Text}${getString(R.string.percentage)}"
        binding.textEquation.text = String.format(
            "%s %s %s", // yer tutucular
            getFormattedDisplayValue(value = getInputValueOne()),
            getOperatorSymbols(),
            input2Text
        )
    }

    private fun updateInputOnDisplay(){
        if (result == null){
            binding.textEquation.text = null
        }
            binding.textInput.text = equation
    }

    private fun getInputValueOne() = inputValueOne ?: 0.0
    private fun getInputValueTwo() = inputValueTwo ?: 0.0

    private fun getOperatorSymbols() : String{
        return when(currentOperator!!){
            Operator.ADDITION -> getString(R.string.addition)
            Operator.MULTIPLICATION -> getString(R.string.multiplication)
            Operator.SUBTRACTION -> getString(R.string.subtraction)
            Operator.DIVISION -> getString(R.string.division)
        }

    }

    private fun getFormattedDisplayValue(value: Double?): String? {
        val originalValue = value ?: return null
        return if (originalValue % 1 == 0.0) {
            originalValue.toInt().toString()
        } else {
            originalValue.toString()
        }
    }

    enum class Operator() {
        ADDITION, SUBTRACTION, DIVISION, MULTIPLICATION
    }


    private companion object{
        const val DECIMAL_POINT = "."
        const val ZERO = "0"
        const val DOUBLE_ZERO = "00"
        const val MINUS = "-"
    }
}