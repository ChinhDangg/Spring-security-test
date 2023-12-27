let e = document.getElementById('email');
let p = document.getElementById('password');
let b = document.getElementById("sign-in-submit");

e.addEventListener('input', checkRequiredInput);
p.addEventListener('input', checkRequiredInput);

function checkRequiredInput() {
    b.disabled = !(e.value && p.value)
}

var jwt = '';
b.addEventListener("click", function() {
    console.log(e.value);
    console.log(p.value);
    var data = {
        email: e.value,
        password: p.value
    };
    $.ajax({
        url: '/authentication/authenticate',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(result) {
            jwt = result.token;
            console.log(jwt);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            displayErrorMessage();
            console.log('Error', errorThrown);
        },
        complete: function() {
            $(b).prop('disabled', true);
            $(p).val('');
        }
    });
});

function displayErrorMessage() {
    document.getElementById('login-error').style.display = 'block';
}