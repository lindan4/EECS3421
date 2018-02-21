select distinct YONE.TITLE
from YRB_BOOK YONE
where (select count(CLUB) 
from YRB_OFFER
where TITLE = YONE.TITLE) = (select count(*) from YRB_CLUB);