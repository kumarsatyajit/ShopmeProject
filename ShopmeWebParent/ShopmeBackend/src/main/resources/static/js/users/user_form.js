document.querySelector('#showPassword').addEventListener('click', function (event) {
  const isCheck = event.target.checked;
  const passwordField = document.querySelector('#password');
  if (isCheck)
    passwordField.type = 'text';
  else
    passwordField.type = 'password';
});

// validate User email
function checkEmailUnique(form) {
  const id = $("#id").val();
  const email = $("#email").val();
  const csrf = $("input[name='_csrf']").val();

  let editUserEmailPromise;
  let newUserEmailPromise;

  if (id) {
    editUserEmailPromise = emailNew(id, email, csrf);
    $.when(editUserEmailPromise).done(function (editUserEmailResponse) {
      if (editUserEmailResponse === "OK") {
        form.submit();
      } else if (editUserEmailResponse === "New") {
        newUserEmailPromise = emailUnique(email, csrf);
        $.when(newUserEmailPromise).done(function (newUserEmailResponse) {
          if (newUserEmailResponse === "OK") {
            form.submit();
          } else if (newUserEmailResponse === "Duplicate") {
            emailWarningDialog(email);
          }

        }).fail(function (error) {
          errorDialogBox();
        })
      }
    }).fail(function (error) {
      errorDialogBox();
    });
  } else {
    newUserEmailPromise = emailUnique(email, csrf);
    $.when(newUserEmailPromise).done(function (newUserEmailResponse) {
      if (newUserEmailResponse === "OK") {
        form.submit();
      } else if (newUserEmailResponse === "Duplicate") {
        emailWarningDialog(email);
      }

    }).fail(function (error) {
      errorDialogBox();
    });
  }

  return false;
}

function emailUnique(email, csrf) {
  const url = window.isEmailUniqueUrl;
  console.log(url);
  const params = { email: email, _csrf: csrf };

  return $.post(url, params);
}

function emailNew(id, email, csrf) {
  const url = window.isEmailNewUrl;
  const params = { id: id, email: email, _csrf: csrf };
  console.log(url, params);

  return $.post(url, params);
}

document.querySelector("#image").addEventListener('change', function (event) {
  const inputImageField = event.target;
  const file = inputImageField.files[0];

  if (file) {
    const fileSize = bytesToMB(file.size);
    if (fileSize <= 1) {
      const reader = new FileReader();
      reader.onload = function (e) {
        console.log("file loaded successfully.");
        $("#imagePreview").attr('src', e.target.result);
      }
      reader.readAsDataURL(file);
    } else {
      const title = "Warning";
      const message = `Input image size is too big. expected: <strong>1MB or less</strong> received: <strong>${fileSize}</strong>MB`;
      showModalDialog(title, message);
      inputImageField.value = "";
    }
  } else {
    console.log("No file selected");
    $("#imagePreview").attr('src', window.defaultImage);
  }

});

function bytesToMB(bytes) {
  const MB = bytes / (1024 * 1024);
  return MB.toFixed(2);
}

function emailWarningDialog(email) {
  const title = "Warning";
  const message = `There is already an User exist with this email address: <strong>${email}</strong>`;
  showModalDialog(title, message);
}

function showModalDialog(title, message) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#modal").modal('show');
}

function errorDialogBox() {
  const title = "Error";
  const message = "Could not connect to the server";
  showModalDialog(title, message);
}

