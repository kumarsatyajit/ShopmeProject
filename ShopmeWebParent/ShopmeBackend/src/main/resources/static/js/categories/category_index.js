document.querySelector("#modal").addEventListener('shown.bs.modal', function (event) {
  const btn = event.relatedTarget;
  const url = btn.getAttribute('href');
  const name = btn.getAttribute('data-category-name');
  const id = btn.getAttribute('data-category-id');

  const title = "Delete Confirmation";
  const message = `Are you sure, You want to delete this category: <strong>${name}</strong> with ID: <strong>${id}</strong>?`;

  showDeleteModal(title, message, url);
});

function showDeleteModal(title, message, url) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#deleteBtn").attr('href', url);
  $("#modal").modal('show');
}

const redirectMessage = document.querySelector("#success-message");
setTimeout(() => {
  redirectMessage.style.display = 'none';
}, 1500);