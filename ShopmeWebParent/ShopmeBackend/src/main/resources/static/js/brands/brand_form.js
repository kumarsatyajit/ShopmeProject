document.querySelector("#categories").addEventListener('change', function (event) {
  const categories = event.target;
  const selectedOptions = Array.from(categories.selectedOptions).map(option => option.text.replaceAll("--", ""));
  console.log(selectedOptions);

  const chosenCategories = document.querySelector("#chosenCategories");
  chosenCategories.innerHTML = "";

  if (selectedOptions.length > 0) {
    selectedOptions.forEach(option => {
      const span = `<span class="badge text-bg-secondary mx-1">${option}</span>`;
      chosenCategories.innerHTML += span;
    });
  } else {
    chosenCategories.innerHTML = "No Category selected.";
  }
});

document.querySelector("#image").addEventListener('change', function (event) {
  const imageInputField = event.target;
  const file = imageInputField.files[0];

  if (file) {
    const fileSize = bytesToMb(file.size);

    if (fileSize < 1) {
      const reader = new FileReader();
      reader.onload = (e) => {
        $("#brandImagePreview").attr('src', e.target.result);
      }

      reader.readAsDataURL(file);
    } else {
      imageInputField.value = "";
      $("#brandImagePreview").attr('src', window.defaultImage);

      const title = "Warning";
      const message = `File size is too big, limit: <b>1MB</b> file: <b>${fileSize}</b>MB`;
      showModalDialog(title, message);
    }
  } else {
    imageInputField.value = "";
    $("#brandImagePreview").attr('src', window.defaultImage);
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

function resetBrandForm() {
  $("#brandImagePreview").attr('src', window.defaultImage);
}

document.querySelector("#brandForm").addEventListener('submit', function (event) {
  event.preventDefault();

  const form = event.target;
  const id = $("#id").val();
  const name = $("#name").val();
  const csrf = $("input[name='_csrf']").val();

  let namePromise;

  if (id) {
    namePromise = checkBrandNameAssociatedWithId(name, id, csrf);
  } else {
    namePromise = checkBrandNameUnique(name, csrf);
  }

  $.when(namePromise).done(function (nameResponse) {
    console.log(nameResponse);
    if (nameResponse === "Ok") {
      form.submit();
    } else if (nameResponse === "New") {
      namePromise = checkBrandNameUnique(name, csrf);

      $.when(namePromise).done(function (nameResponse) {
        if (nameResponse === "Ok") {
          form.submit();
        } else if (nameResponse === "Duplicate") {
          warningDialog("name", name);
        }
      }).fail(function (error) {
        console.log(error);
        errorDialog();
      })
    } else if (nameResponse === "Duplicate") {
      warningDialog("name", name);
    }
  }).fail(function (error) {
    console.log(error);
    errorDialog();
  });

});

function checkBrandNameUnique(name, csrf) {
  const url = window.isBrandNameUniqueUrl;
  const params = { name: name, _csrf: csrf };

  return $.post(url, params);
}

function checkBrandNameAssociatedWithId(name, id, csrf) {
  const url = window.isBrandNameAssociatedWithIdUrl;
  const params = { name: name, id: id, _csrf: csrf };

  return $.post(url, params);
}

function errorDialog() {
  const title = "Error";
  const message = "Could not connect to the server";

  showModalDialog(title, message);
}

function warningDialog(field, value) {
  const title = "Warning";
  const message = `There is already a Brand exist with <b>${field}</b> : <b>${value}</b>`;

  showModalDialog(title, message);
}

document.addEventListener('DOMContentLoaded', function () {
  const categoriesDropdown = document.getElementById('categories');
  const options = categoriesDropdown.options;
  const selectedCategories = window.selectedCategories;

  for (let i = 0; i < options.length; i++) {
    const option = options[i].innerHTML.replaceAll('--', '');
    if (selectedCategories.includes(option)) {
      options[i].selected = true;
    }
  }

  const selectedOptions = Array.from(categoriesDropdown.selectedOptions).map(option => option.text.replaceAll("--", ""));
  
  const chosenCategories = document.querySelector("#chosenCategories");
  chosenCategories.innerHTML = "";

  if (selectedOptions.length > 0) {
    selectedOptions.forEach(option => {
      const span = `<span class="badge text-bg-secondary mx-1">${option}</span>`;
      chosenCategories.innerHTML += span;
    });
  } else {
    chosenCategories.innerHTML = "No Category selected.";
  }


})