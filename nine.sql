select CID, cast (((days(max(DATUM)) - days(min(DATUM))) / (count(*) - 1)) as decimal(7, 2)) as avg_gap
from
(select distinct D1.CID, cast (D1.WHEN as date) DATUM
from YRB_PURCHASE D1, YRB_PURCHASE D2
where D1.CID = D2.CID and D1.WHEN <> D2.WHEN
order by CID asc)
group by CID;