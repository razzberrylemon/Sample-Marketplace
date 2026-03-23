function regDisp() {

    if(document.getElementById("customer").checked) {
        document.getElementById("regTitle").innerHTML = "Customer Registration";
    } else if (document.getElementById("admin").checked) {
        document.getElementById("regTitle").innerHTML = "Admin Registration";
    }
}

function loginDisp() {
    if(document.getElementById("customer").checked) {
        document.getElementById("loginTitle").innerHTML = "Customer Login";
    } else if (document.getElementById("admin").checked) {
        document.getElementById("loginTitle").innerHTML = "Admin Login";
    }
}
