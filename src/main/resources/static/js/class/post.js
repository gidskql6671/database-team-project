
async function postComment(lectureCode, sectionCode, postId) {
  const form = document.getElementById("form_comment");

  const content = form.comment.value;

  const url = `http://localhost:8080/api/class/${lectureCode}/${sectionCode}/post/${postId}/comment`
  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({content})
  });

  if (response.ok) {
    location.reload();
  }
  else {
    const reason = (await response.json()).msg

    alert(`댓글 작성이 실패했습니다.\n사유: ${reason}`);
  }

  return false;
}