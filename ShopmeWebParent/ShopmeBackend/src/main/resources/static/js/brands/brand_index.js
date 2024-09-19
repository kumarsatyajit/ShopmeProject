const redirectMessage = document.querySelector("#success-message");
setTimeout(() => {
  redirectMessage.style.display = 'none';
}, 1500);

document.querySelector("#modal").addEventListener('shown.bs.modal', function (event) {
  const btn = event.relatedTarget;
  const url = btn.getAttribute('href');
  const name = btn.getAttribute('data-brand-name');
  const id = btn.getAttribute('data-brand-id');

  const title = "Delete Confirmation";
  const message = `Are you sure, You want to delete this Brand: <strong>${name}</strong> with ID: <strong>${id}</strong>?`;

  showDeleteModal(title, message, url);
});

function showDeleteModal(title, message, url) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#deleteBtn").attr('href', url);
  $("#modal").modal('show');
}