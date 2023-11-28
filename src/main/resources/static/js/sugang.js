
async function takeClass() {
    const loginForm = document.getElementById("sugang_form");

    const fullCode = loginForm.full_code.value;
    if (fullCode.length <= 8) {
      alert("전체 코드를 제대로 입력해주세요.");
      return false;
    }

    const response = await fetch("http://localhost:8080/api/sugang", {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        lectureCode: fullCode.substring(0, 8),
        sectionCode: fullCode.substring(8)
      })
    });

    if (response.ok) {
        location.reload();
    }
    else {
      const reason = (await response.json()).msg

      alert(`수강신청이 실패했습니다.\n사유: ${reason}`);
    }

    return false;
}

async function untakeClass(lectureCode, sectionCode) {
    const response = await fetch("http://localhost:8080/api/sugang", {
      method: 'DELETE',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({lectureCode: lectureCode, sectionCode: sectionCode})
    });

    if (response.ok) {
        location.reload();
    }
    else {
      alert(`수강신청이 실패했습니다.`);
    }
}