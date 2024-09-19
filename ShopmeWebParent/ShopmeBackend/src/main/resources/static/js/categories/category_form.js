
/* Validate Name & Alias */

document.querySelector("#categoryForm").addEventListener('submit', function (event) {
  event.preventDefault();

  const form = event.target;
  const id = $("#id").val();
  const name = $("#name").val();
  const alias = $("#alias").val();
  const csrf = $("input[name='_csrf']").val();

  

  let namePromise;
  let aliasPromise;

  if (id) {
    namePromise = isNameAssociatedWithIdFunction(name, id, csrf);
    aliasPromise = isAliasAssociatedWithIdFunction(alias, id, csrf);
  } else {
    namePromise = isNameUniqueFunction(name, csrf);
    aliasPromise = isAliasUniqueFunction(alias, csrf);
  }

  $.when(namePromise, aliasPromise).done(function (nameResponse, aliasResponse) {

    if (nameResponse[0] === "Ok" && aliasResponse[0] === "Ok") {
      form.submit();
    } else if (nameResponse[0] === "Ok" && aliasResponse[0] === "New") {
      aliasPromise = isAliasUniqueFunction(alias, csrf);

      $.when(aliasPromise).done(function (aliasResponse) {
        if (aliasResponse === "Ok") {
          form.submit();
        } else if (aliasResponse === "Duplicate") {
          warningDialog("alias", alias);
        }

      }).fail(function (error) {
        console.log(error);
        errorDialog();
      })
    } else if (nameResponse[0] === "New" && aliasResponse[0] === "Ok") {
      namePromise = isNameUniqueFunction(name, csrf);

      $.when(namePromise).done(function (nameResponse) {
        if (nameResponse === "Ok") {
          form.submit();
        } else if (nameResponse === "Duplicate") {
          warningDialog("name", name);
        }

      }).fail(function (error) {
        console.log(error);
        errorDialog();
      });
    } else if (nameResponse[0] === "Ok" && aliasResponse[0] === "Duplicate") {
      warningDialog("alias", alias);
    } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Ok") {
      warningDialog("name", name);
    } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Duplicate") {
      warningDialog("name & alias", `${name}, ${alias}`);
    }

  }).fail(function (error) {
    console.log(error);
    errorDialog();
  });

});

function isNameUniqueFunction(name, csrf) {
  const url = window.isNameUnique;
  const params = { name: name, _csrf: csrf };
  
  return $.post(url, params);
}

function isAliasUniqueFunction(alias, csrf) {
  const url = window.isAliasUnique;
  const params = { alias: alias, _csrf: csrf };
 
  return $.post(url, params);
}

function isNameAssociatedWithIdFunction(name, id, csrf) {
  const url = window.isNameAssociatedWithId;
  const params = { name: name, id: id, _csrf: csrf };
 
  return $.post(url, params);
}

function isAliasAssociatedWithIdFunction(alias, id, csrf) {
  const url = window.isAliasAssociatedWithId;
  const params = { alias: alias, id: id, _csrf: csrf };

  return $.post(url, params);
}

function errorDialog() {
  const title = "Error";
  const message = "Could not connect to the server";

  showModalDialog(title, message);
}

function warningDialog(field, value) {
  const title = "Warning";
  const message = `There is already a Category exist with <b>${field}</b> : <b>${value}</b>`;

  showModalDialog(title, message);
}


/* image section */

document.querySelector("#image").addEventListener('change', function (event) {
  const imageInputField = event.target;
  const file = imageInputField.files[0];

  if (file) {
    const fileSize = bytesToMb(file.size);

    if (fileSize < 1) {
      const reader = new FileReader();
      reader.onload = (e) => {
        $("#categoryImagePreview").attr('src', e.target.result);
      }

      reader.readAsDataURL(file);
    } else {
      imageInputField.value = "";
      $("#categoryImagePreview").attr('src', window.defaultImage);

      const title = "Warning";
      const message = `File size is too big, limit: <b>1MB</b> file: <b>${fileSize}</b>MB`;
      showModalDialog(title, message);
    }
  } else {
    imageInputField.value = "";
    $("#categoryImagePreview").attr('src', window.defaultImage);
  }

  event.defaultPrevented;
});

function showModalDialog(title, message) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#modal").modal('show');
}

function bytesToMb(bytes) {
  const mb = bytes / (1024 * 1024);
  return mb.toFixed(2);
}

function resetCategoryForm(){
  $("#categoryImagePreview").attr('src', window.defaultImage);
}
