var display = document.querySelector("#display");
var number = "";
var operator = "";
var result = "";


var buttons = document.querySelectorAll("[class*=\"btn_\"]");

[...buttons].forEach((value, index, array) => {
    value.addEventListener("click", e => {
        let buttonText = e.target.innerText;

        if(buttonText == "=") {
            display.innerText = "";
            number = calcResult(number, operator, result);
            operator = "";
            result = "";
            display.innerText = number + operator + result;
            return;
        }

        switch(buttonText) {
            case "/":
            case "*":
            case "+":
            case "-":
                operator = buttonText;
                display.innerText = number + operator + result;
                return;
            break;
        }


        if(operator == "") {
            number += buttonText;
        } else {
            result += buttonText;
        }

        display.innerText = number + operator + result;
    });
});

function calcResult(number, operator, number2) {
    firstnumber = parseInt(number);
    secondnumber = parseInt(number2);


    switch (operator) {
        case "/":
            return number / number2;
        case "*":
            return number * number2;
        case "+":
            return number + number2;
        case "-":
            return number - number2;
        default:
            return number + number2;
    }
}