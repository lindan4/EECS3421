select cast (avg(MAXPERCAT) as decimal(5, 2)) avg_cost
from
(select CAT, min(PRICE) as MAXPERCAT
from (select YRB_BOOK.TITLE, YRB_OFFER.PRICE, YRB_BOOK.CAT
from YRB_OFFER, YRB_BOOK
where YRB_BOOK.TITLE = YRB_OFFER.TITLE and (YRB_BOOK.TITLE, YRB_OFFER.PRICE) in
(select TITLE, min(PRICE)
from YRB_OFFER
group by TITLE))
group by CAT);