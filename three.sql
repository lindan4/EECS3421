select TITLE, YEAR, CAT
from YRB_BOOK
where (YEAR, CAT) in 
(select min(YEAR), CAT
from YRB_BOOK
group by CAT);