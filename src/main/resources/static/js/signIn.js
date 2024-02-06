let e = document.getElementById('email');
let p = document.getElementById('password');
let b = document.getElementById("sign-in-submit");

e.addEventListener('input', checkRequiredInput);
p.addEventListener('input', checkRequiredInput);

function checkRequiredInput() {
    b.disabled = !(e.value && p.value)
}

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
        success: function(data, textStatus, jqXHR) {
            var redirect = new URLSearchParams(window.location.search).get('r');
            window.location.href = (redirect !== null) ? redirect : jqXHR.getResponseHeader('Location');
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