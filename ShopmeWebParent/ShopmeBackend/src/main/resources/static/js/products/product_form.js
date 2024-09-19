document.addEventListener('DOMContentLoaded', function () {
  const categoryId = document.querySelector('#categories').getAttribute('data-selected-category');
  const brandId = document.querySelector("#brand").value;

  if (brandId) {
    const csrf = $("input[name='_csrf']").val();

    categoriesPromise = getAssociatedCategories(brandId, csrf);
    $.when(categoriesPromise).done(function (categoriesResponse) {
      const categoryContainer = $("#categories");
      categoryContainer.html(`<option value="">[No Category]</option>`); // Clear the container first

      // Populate the dropdown with categories
      for (let i = 0; i < categoriesResponse.length; i++) {
        let category = categoriesResponse[i];
        const selected = category.id == categoryId ? 'selected' : '';
        const option = `<option value="${category.id}" ${selected}>${category.name}</option>`;
        categoryContainer.append(option);
      }

    }).fail(() => {
      errorDialog();
    });
  }

});



document.querySelector('#productForm').addEventListener('submit', function (event) {
  event.preventDefault();

  const form = event.target;
  const id = $("#id").val();
  const name = $("#name").val();
  const alias = $("#alias").val();
  const csrf = $("input[name='_csrf']").val();

  let namePromise;
  let aliasPromise;

  if (id) {
    namePromise = checkProductNameIsUnique(name, id, csrf);
    aliasPromise = checkProductAliasIsUnique(alias, id, csrf);

    $.when(namePromise, aliasPromise).done(function (nameResponse, aliasResponse) {
      if (nameResponse[0] === "Ok" && aliasResponse[0] === "Ok") {
        form.submit();
      } else if (nameResponse[0] === "Ok" && aliasResponse[0] === "New") {
        aliasPromise = checkProductAlias(alias, csrf);

        $.when(aliasPromise).done(function (aliasResponse) {
          if (aliasResponse === "Ok") {
            form.submit()
          } else if (aliasResponse === "Duplicate") {
            warningDialog("alias", alias);
          }
        }).fail(() => {
          errorDialog();
        })
      } else if (nameResponse[0] === "New" && aliasResponse[0] === "Ok") {
        namePromise = checkProductName(name, csrf);

        $.when(namePromise).done(function (nameResponse) {
          if (nameResponse === "Ok") {
            form.submit()
          } else if (nameResponse === "Duplicate") {
            warningDialog("name", name);
          }
        }).fail(() => {
          errorDialog();
        })
      } else if (nameResponse[0] === "New" && aliasResponse[0] === "New") {
        namePromise = checkProductName(name, csrf);
        aliasPromise = checkProductAlias(alias, csrf);

        $.when(namePromise, aliasPromise).done(function (nameResponse, aliasResponse) {
          if (nameResponse[0] === "Ok" && aliasResponse[0] === "Ok") {
            form.submit();
          } else if (nameResponse[0] === "Ok" && aliasResponse[0] === "Duplicate") {
            warningDialog("alias", alias);
          } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Ok") {
            warningDialog("name", name);
          } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Duplicate") {
            warningDialog("name & alias", `${name}, ${alias}`);
          }

        }).fail(() => {
          errorDialog();
        })
      }


    }).fail(() => {
      errorDialog();
    })

  } else {
    if (alias) {
      namePromise = checkProductName(name, csrf);
      aliasPromise = checkProductAlias(alias, csrf);

      $.when(namePromise, aliasPromise).done(function (nameResponse, aliasResponse) {
        if (nameResponse[0] === "Ok" && aliasResponse[0] === "Ok") {
          form.submit();
        } else if (nameResponse[0] === "Ok" && aliasResponse[0] === "Duplicate") {
          warningDialog("alias", alias);
        } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Ok") {
          warningDialog("name", name);
        } else if (nameResponse[0] === "Duplicate" && aliasResponse[0] === "Duplicate") {
          warningDialog("name & alias", `${name}, ${alias}`);
        }

      }).fail(() => {
        errorDialog();
      })
    } else {
      namePromise = checkProductName(name, csrf);

      $.when(namePromise).done(function (nameResponse) {
        if (nameResponse === "Ok") {
          form.submit()
        } else if (nameResponse === "Duplicate") {
          warningDialog("name", name);
        }
      }).fail(() => {
        errorDialog();
      })
    }
  }
});

function checkProductName(name, csrf) {
  const url = window.isProductNameUniqueUrl;
  const params = { name: name, _csrf: csrf };

  return $.post(url, params);
}

function checkProductAlias(alias, csrf) {
  const url = window.isProductAliasUniqueUrl;
  const params = { alias: alias, _csrf: csrf };

  return $.post(url, params);
}

function checkProductNameIsUnique(name, id, csrf) {
  const url = window.isProductNameAssociatedWithIdUrl;
  const params = { name: name, id: id, _csrf: csrf };

  return $.post(url, params);
}

function checkProductAliasIsUnique(alias, id, csrf) {
  const url = window.isProductAliasAssociatedWithIdUrl;
  const params = { alias: alias, id: id, _csrf: csrf };

  return $.post(url, params);
}

function showModalDialog(title, message) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#modal").modal('show');
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



document.querySelector("#brand").addEventListener('change', function (event) {
  const brandId = event.target.value;
  if (brandId) {
    const csrf = $("input[name='_csrf']").val();

    categoriesPromise = getAssociatedCategories(brandId, csrf);
    $.when(categoriesPromise).done(function (categoriesResponse) {
      const categoryContainer = $("#categories");
      categoryContainer.html(`<option value="">[No Category]</option>`);

      for (let i = 0; i < categoriesResponse.length; i++) {
        let category = categoriesResponse[i];
        const option = `<option value="${category.id}">${category.name}</option>`;
        categoryContainer.append(option);
      }

    }).fail(() => {
      errorDialog();
    })
  } else {
    const categoryContainer = $("#categories");
    categoryContainer.html(`<option value="">[No Category]</option>`);
  }


});

function getAssociatedCategories(brandId, csrf) {
  const url = window.getAssociatedCategoriesUrl;
  const params = { brandId: brandId, _csrf: csrf };

  return $.post(url, params);
}


tinymce.init({
  selector: '#shortDescription, #fullDescription',  // Target both textareas by their IDs
  height: 300,  // Adjust the height as necessary
  plugins: 'lists link image preview code',  // Reduced to only essential plugins
  toolbar: 'undo redo | bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist | link image | preview code',  // Essential toolbar options
  menubar: false,  // Disable the menubar for simplicity
  toolbar_sticky: true,
  image_advtab: false,  // Simplify the image options
  content_css: '//www.tiny.cloud/css/codepen.min.css',  // Optional styling
  branding: false  // Removes the TinyMCE branding
});

function addDetailsField() {
  const details = `
   <div class="row my-3">
    <label class="col-form-label col-sm-1 text-center fw-semibold">Name</label>
    <div class="col-sm-4">
      <input type="text" name="detailNames" class="form-control">
    </div>
    <label class="col-form-label col-sm-1 text-center fw-semibold">Value</label>
    <div class="col-sm-4">
      <input type="text" name="detailValues" class="form-control">
    </div>
    <div class="col-sm-2">
      <button type="button" class="fas fa-times-circle fa-2x no-border" onclick="removeDetail(this)"></button>
    </div>
  </div>
  `;

  const container = $("#details-container");
  container.append(details);

}

function removeDetail(button) {
  var row = button.closest('.row');
  if (row) {
    row.remove();
  }
}


function setupImageUpload(imageInputFieldId, previewImageId, maxFileSize) {
  const defaultImage = `${$(`#${previewImageId}`).attr('data-default-image-url')}`;
  const imageInputField = $(`#${imageInputFieldId}`)[0];
  const previewImage = $(`#${previewImageId}`)[0];

  console.log(defaultImage, imageInputField, previewImage);

  const file = imageInputField.files[0];

  if (file) {
    const fileSize = bytesToMb(file.size);

    if (fileSize <= maxFileSize) {
      const reader = new FileReader();
      reader.onload = (e) => {
        $(`#${previewImageId}`).attr('src', e.target.result);
      }

      reader.readAsDataURL(file);
    } else {
      $(`#${imageInputFieldId}`).val('');
      $(`#${previewImageId}`).attr('src', defaultImage);

      const title = "Warning";
      const message = `File size is too big, limit: <b>1MB</b> file: <b>${fileSize}</b>MB`;
      showModalDialog(title, message);
    }
  } else {
    $(`#${imageInputFieldId}`).val('');
    $(`#${previewImageId}`).attr('src', defaultImage);
  }
}

function bytesToMb(bytes) {
  const mb = bytes / (1024 * 1024);
  return mb.toFixed(2);
}

function addExtraImageField(inputField) {
  const parentDiv = inputField.parentNode;
  const imgElement = parentDiv.querySelector('img');
  const file = inputField.files[0];

  if (file) {
    const fileSize = bytesToMb(file.size);

    if (fileSize <= 1) {
      const reader = new FileReader();
      reader.onload = (e) => {
        imgElement.setAttribute('src', e.target.result);
      }

      reader.readAsDataURL(file);
    } else {
      const title = "Warning";
      const message = `File size is too big, limit: <b>1MB</b> file: <b>${fileSize}</b>MB`;
      showModalDialog(title, message);
    }
  }


  const container = document.getElementById('imageContainer');
  let currentNumber = inputField.getAttribute('data-number');
  const url = inputField.getAttribute('data-default-image-url');

  const newField = document.createElement('div');
  newField.classList.add('col', 'border', 'm-3', 'p-2', 'position-relative');
  newField.innerHTML = `
        <button type="button" class="fas fa-times-circle no-border position-absolute top-0 end-0 p-1" 
          aria-label="Close" onclick="removeImageField(this)"></button>
        <div>
          <div><label class="fs-6 fw-semibold">Extra Image #${++currentNumber}</label></div>
          <img src="${url}" alt="product default image" class="img-fluid img-thumbnail">
          <input type="file" name="images" class="form-control mt-2"
            data-number=${currentNumber} data-default-image-url=${url}
            onchange="addExtraImageField(this)">
        </div>
  `;

  container.appendChild(newField);
}

function removeImageField(button) {
  var div = button.closest('div');
  if (div) {
    div.remove();
  }
}
