
document.getElementById('startDateTime').value= new Date().toISOString().slice(11, 16);
document.getElementById('endDateTime').value= new Date().toISOString().slice(11, 16);

async function reserve() {
  const form = document.getElementById("form_reservation");

  const buildingNumber = form.buildingNumber.value;
  const roomCode = form.roomCode.value;
  const startDateTime = form.startDateTime.value;
  const endDateTime = form.endDateTime.value;

  const response = await fetch("http://localhost:8080/api/classroom", {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      buildingNumber: buildingNumber,
      roomCode: roomCode,
      startDateTime: startDateTime,
      endDateTime: endDateTime
    })
  });

  if (response.ok) {
    location.reload();
  }
  else {
    const reason = (await response.json()).msg

    alert(`강의실 예약이 실패했습니다.\n사유: ${reason}`);
  }

  return false;
}