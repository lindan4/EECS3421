select distinct YRB_CLUB.CLUB, DESC
from YRB_OFFER, YRB_CLUB
where YRB_CLUB.CLUB = YRB_OFFER.CLUB and (TITLE, PRICE) in
  (select TITLE, max(PRICE)
    from YRB_OFFER
    group by TITLE);
