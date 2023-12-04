var productsList = new Array();

class Product {
    constructor(name, price, desc, place, img, pid) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.place = place;
        this.img = img;
        this.pid = pid
        this.display = displayProduct;

    }
}


$(document).ready(function () {
    //products();
    $('#popularProducts').DataTable({
        "ordering": false,
        "paging": false,
        "searching": false,
        "info": false
    });

})

function handleErrors(response) {
    console.log(response.status);
    if (response.redirected) {
        window.open(response.url, "_self");
        return false
    }
    else if (response.status === 403) {
        localStorage.removeItem("token");
        redirect("/");
    }
    return response;
}

//POST request with token
async function postData(url = "", data = {}) {
    let options = {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.token
        },
        body: JSON.stringify(data),
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}
//POST request without token
async function auxPost(url = "", data = {}) {
    let options = {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        body: JSON.stringify(data),
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}



//GET request with token
async function getData(url = "", data = {}) {
    let options = {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.token
        },
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}

//GET request without token
async function _getData(url = "", data = {}) {
    let options = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"

        },
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}

//DELETE request with token
async function deleteData(url) {
    let options = {
        method: "DELETE",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.token
        }
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}

//PUT request with token
async function putData(url = "", data = {}) {

    let options = {
        method: "PUT",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.token
        },
        body: JSON.stringify(data),
    };
    const response = await fetch(url, options).then(handleErrors);
    return response.json();
}



function displayProduct(idx) {
    console.log(idx);
}

function displayProducts() {
    productsList[0].display(0);

}

async function products() {
    const returned_result = await _getData("./products/all/products").then((result) => {
        if (result['success'] === true) {
            result.data.forEach(function (item) {
                let product = new Product(item["name"], item["price"], item["description"], item["location"], item["image"], item["pid"])
                productsList.push(product)
            });
        }
    });

}

/* Search functions */
function checkString(str, ele_txt) {
    str = str.toLowerCase();
    return (ele_txt.toLowerCase().indexOf(str) > -1);
}

function filterEntries() {
    let strSearch = document.getElementById("search-icon").value;
    document.querySelectorAll("article.framed").forEach(
        function (article_ele) {
            let art = document.getElementById(article_ele.id);
            let h = article_ele.querySelectorAll("h3,h4");
            if (strSearch.length > 0 && !checkString(strSearch, h[0].innerHTML) && !checkString(strSearch, h[1].innerHTML)) {
                art.style.display = "none";
            } else if (strSearch.length == "") {
                $("article").unmark();
                art.style.display = "";
            } else {
                art.style.display = "";
                mark_text();
            }
        })

}

/* Mark terms functions */
function mark_text() {
    let strSearch = document.getElementById("search-icon").value;
    var patt = /"(.*?)"/gi;
    var matches = new Array();
    while ((match = patt.exec(strSearch)) !== null) {
        matches.push(match[1]);
    }
    var txt = strSearch.replace(patt, "");
    matches = matches.concat(txt.trim().split(" "));
    matches.forEach(function (term) {
        var regex_text = new RegExp("\\b(" + term + ")\\b", "i"); // RegExp
        $("article.framed .prod_right h3,h4").each(function (i, e) {
            console.log(e);
            $(e).markRegExp(regex_text, { className: "orange", accuracy: "exactly" });
        });
    });
}
/* End Search functions */

function create_booking() {

}


/* confirm purchase */
async function confirmProduct() {
    let oid = document.getElementById("oid").value;
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, confirm order!',
    }).then((result) => {
        if (result.isConfirmed) {
            getData("./booking/confirm/" + oid).then((response_data) => {
                if (response_data['success'] === true) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Your order is confirmed!',
                        showConfirmButton: false,
                        timer: 2000
                    })
                    return redirect("/");
                } else {
                    return ($("#info").html("<strong>Wrong!</strong> " + " Failed Operation!"));
                }
            });

        } else {
            Swal.fire('Order are not cancelled', '', 'info')
            return redirect("/");
        }
    })
};


/* Login */
async function login() {
    let userName = $("#username").val().trim();
    let passWord = $("#password").val().trim();
    let form = new FormData();
    form.append("username", userName);
    form.append("password", passWord);
    const data = new URLSearchParams();
    for (const pair of form) {
        data.append(pair[0], pair[1]);
    }
    if (isUserNameValid(userName)) {
        const response_data = await fetch("./auth/token", { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: data }).then(response => response.json());
        if (response_data['success'] === true) {
            let res = response_data["token_data"];
            localStorage.setItem('token', res['access_token']);
            window.open(res['host_url'], "_self");
        } else {

            return ($("#info").html("<strong>Wrong!</strong> " + " Invalid Credentials Error!"));
        }

    } else {

        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid letters for username!"));
    }

}

/* Google Auth functions */
function getGoogleToken() {
    let clientId = document.getElementById("gid").value;
    google.accounts.id.initialize({
        client_id: clientId,
        callback: handleCredentialResponse,
        prompt_parent_id: "g_id"

    });

    google.accounts.id.prompt((notification) => {
        if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
            console.log(notification.getNotDisplayedReason());
            return ($("#info").html("<strong>Please use another login method!</strong> " + " Google Authentication not available!"));
        }
    });
}
//Logout
async function onSignout() {
    const returned_result = await getData("/auth/logout").then((result) => {
        console.log(result);
        if (result["success"] === true) {
            localStorage.removeItem("token");
            google.accounts.id.disableAutoSelect();
            return redirect("/");
        }
    });

}

//Decode Google credentials
async function handleCredentialResponse(response) {
    const responsePayload = decodeJwtResponse(response.credential);
    let data = { "username": responsePayload.given_name, "email": responsePayload.email };
    auxPost("./auth/google", data).then((response_data) => {
        if (response_data['success'] === true) {
            let res = response_data["token_data"];
            localStorage.setItem('token', res['access_token']);
            window.open(res['host_url'], "_self");
        } else {

            return ($("#info").html("<strong>Wrong!</strong> " + " Invalid Credentials!"));
        }
    });

}

/* Create new user */
async function signUP() {
    let username_ = $("#username_").val().trim();
    let pass = $("#pass").val().trim();
    let pass_ = $("#pass_").val().trim();
    let email = $("#email_").val().trim();
    let tel = $("#tel").val().trim();
    if (!is_Input_Error(username_, email, pass, pass_, tel)) {
        let obj = { "username": username_, "email": email, "password": pass, "telephone": tel };
        const res = await auxPost('./users/signup', obj);
        if (res["success"] === true) {
            Swal.fire({
                icon: "success",
                timerProgressBar: true,
                title: "User with ID " + res["userid"] + " has been created!",
                showConfirmButton: false,
                timer: 2000,
            });
            $("#info").empty();
            hide_add_entry("add_entry");

        } else {
            return ($("#info").html("Email/Username already registered"));
        }
    }
}


/* Admin functions */
async function getBookings() {
    const returned_result = await getData("./booking/all").then((result) => {
        console.log(result);
    });
}

function orderChart() {
    var bookingsPerDay;//order per day
    var bookingChartLabels = []
    var bookingChartData = []

    $.each(bookingsPerDay, function (key, value) {
        var dateObj = new Date(key);
        console.log(dateObj)
        bookingChartLabels.push(dateObj);
        bookingChartData.push(value);
    });

    /** OrderChart creation */
    var ctx = document.getElementById("orderChart").getContext('2d');
    var myLineChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: bookingChartLabels,
            datasets: [{
                label: 'First dataset',
                backgroundColor: '#5DA5DA',
                borderColor: '#5DA5DA',
                data: bookingChartData,

            }]
        },
        options: {
            legend: {
                display: false
            },
            tooltips: {
                callbacks: {
                    label: function (tooltipItem) {
                        return tooltipItem.yLabel;
                    }
                }
            },
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        unit: 'day',
                        unitStepSize: 3,
                        displayFormats: {
                            'day': 'DD MMM YY'
                        }
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });


}




/* Helper functions */

function redirect(path_string) {
    window.location.replace(path_string);
}


function decodeJwtResponse(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

function isUserNameValid(username) {
    /* 
      Usernames can only have: 
      - Lowercase Letters (a-z) 
      - Numbers (0-9)
      - Dots (.)
      - Underscores (_)
    */
    const res = /^[a-z0-9_\.]+$/.exec(username);
    const valid = !!res;
    return valid;
}
function hide_update_entry(id) {
    var element = document.getElementById(id);
    element.style.display = "none";
}

function show_update_entry(id) {
    var element = document.getElementById(id);
    element.style.display = "";
}

function hide_add_entry(id) {
    var element = document.getElementById(id);
    element.style.display = "none";
}

function show_add_entry(id) {
    let username_ = $("#username_").val(" ");
    let pass = $("#pass").val(" ");
    let pass_ = $("#pass_").val(" ");
    let email = $("#email_").val(" ");
    let tel = $("#tel").val(" ");
    var element = document.getElementById(id);
    element.style.display = "";
}


function isValidTel(tel) {
    // check for allowed characters using a regular expression
    var re = /^[0-9()+\-\s]*$/
    return re.test(tel);
}

function is_valid_letter(name) {
    var re = /^[A-Za-z]+$/;
    return re.test(name);
}

function is_valid_Email(email) {
    return email.match(
        /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );
}
function make_string(length) {
    var result = "";
    var characters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    var charactersLength = characters.length;
    for (var i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}
function is_Input_Error(name, email, password, password_, tel) {
    if (name.length == 0) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Empty username!"));
    }
    else if (email.length == 0) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Empty email field!"));
    }
    else if (tel.length == 0) {

        return ($("#info").html("<strong>Wrong!</strong> " + " Empty Telephone field!"));
    }
    else if (password.length == 0 || password_.length == 0) {
        $("#info").html("<strong>Wrong!</strong> " + " Empty Password Field!");
    }
    else if (password !== password_) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid Credentials.Password mismatch!"));
    }

    // check for valid telephone
    else if (tel.length > 0 && !isValidTel(tel)) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid letters for telephone!"));
    }
    // check for valid email
    else if (email.length > 0 && !is_valid_Email(email)) {
        $("#info").html("<strong>Wrong!</strong> " + " Invalid email address!");
    }
    // check for valid letters
    else if (name.length > 0 && !isUserNameValid(name)) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid letters for username!"));
    }
    // no error
    else {
        return false;
    }
    return true;
}
function email_telephone_Error(email, tel) {
    if (email.length == 0) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Empty email field!"));
    }
    else if (tel.length == 0) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Empty telephone field!"));
    }
    // check for valid email
    else if (email.length > 0 && !is_valid_Email(email)) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid email address!"));
    }
    // check for valid telephone
    else if (tel.length > 0 && !isValidTel(tel)) {
        return ($("#info").html("<strong>Wrong!</strong> " + " Invalid letters for telephone!"));
    }
    // no error
    else {
        return false;
    }
    return true;
}