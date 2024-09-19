const successMessage = document.querySelector('#success-message');
setTimeout(() => {
  successMessage.style.display = 'none';
}, 1500);

document.getElementById('modal').addEventListener('show.bs.modal', function (event) {
  const deleteBtn = event.relatedTarget;
  const url = deleteBtn.getAttribute('href');
  const username = deleteBtn.getAttribute('data-user-name');
  const id = deleteBtn.getAttribute('data-user-id');
  console.log(url, username, id);

  const title = "Delete Confirmation";
  const message = `Are you sure,<br> You want to delete this User: <strong>${username}</strong> with ID: <strong>${id}</strong>?`;
  showDeleteModalDialog(title, message, url);
});

function showDeleteModalDialog(title, message, url) {
  $("#modalHead").text(title);
  $("#modalBody").html(message);
  $("#deleteBtn").attr('href', url);
}