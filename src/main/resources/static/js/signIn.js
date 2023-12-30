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
            var jwt = data.token;
            var redirect_url = jqXHR.getResponseHeader('redirect-url');
            redirectClient(redirect_url, jwt);
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

function redirectClient(redirect_url, jwt) {
    if (redirect_url != null && jwt != '') {
        redirect_url = redirect_url.substring(0,redirect_url.indexOf('?'))
        console.log(redirect_url);
        $.ajax({
            url: redirect_url,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${jwt}`
            },
            success: function(data) {
                console.log('success');
                window.location.href = redirect_url;
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error:', errorThrown);
            }
        });
    }
}

function displayErrorMessage() {
    document.getElementById('login-error').style.display = 'block';
}