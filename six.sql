select *
from YRB_CUSTOMER
where CID not in
(select distinct CID
from YRB_PURCHASE);