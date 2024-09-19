const redirectMessage = document.querySelector("#success-message");
setTimeout(() => {
  redirectMessage.style.display = 'none';
}, 1500);

document.querySelector("#modal").addEventListener('shown.bs.modal', function (event) {
  const btn = event.relatedTarget;
  const url = btn.getAttribute('href');
  const name = btn.getAttribute('data-product-name');
  const id = btn.getAttribute('data-product-id');

  const title = "Delete Confirmation";
  const message = `Are you sure, You want to delete this Product: <strong>${name}</strong> with ID: <strong>${id}</strong>?`;

  showDeleteModal(title, message, url);
});

function showDeleteModal(title, message, url) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#deleteBtn").attr('href', url);
  $("#modal").modal('show');
}

document.querySelectorAll('.link-details').forEach(function(link) {
  link.addEventListener('click', function(event) {
    event.preventDefault();
    const url = event.target.getAttribute('href');
    $("#detailModal").modal('show').find('.modal-content').load(url);
  });
});
