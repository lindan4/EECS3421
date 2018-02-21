(select distinct TITLE
from YRB_OFFER)
except
(select distinct TITLE
from YRB_OFFER
where CLUB='Readers Digest');