const delete_elements = document.getElementsByClassName("delete");
Array.from(delete_elements).forEach(function(element) {
  element.addEventListener('click', function() {
    if(confirm("정말로 삭제하시겠습니까?")) {
      location.href = this.dataset.uri;
    }
  });
});
const recommend_elements = document.getElementsByClassName("recommend");
Array.from(recommend_elements).forEach(function(element) {
  element.addEventListener('click', function() {
    if(confirm("추천하시겠습니까?")) {
      location.href = this.dataset.uri;
    }
  });
});
const page_elements = document.getElementsByClassName("page-link");
Array.from(page_elements).forEach(function(element) {
  element.addEventListener('click', function() {
    document.getElementById('page').value = this.dataset.page;
    document.getElementById('searchForm').submit();
  });
});
const btn_search = document.getElementById("btn_search");
btn_search.addEventListener('click', function() {
  document.getElementById('kw').value = document.getElementById('search_kw').value;
  document.getElementById('page').value = 0;  // 검색버튼을 클릭할 경우 0페이지부터 조회한다.
  document.getElementById('searchForm').submit();
});