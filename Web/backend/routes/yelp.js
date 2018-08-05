import express from 'express';
import yelp from 'yelp-fusion';

let router = express.Router();

let API_KEY = 'Yfi-F5n9DH3m4-SIfDojHTVWS0fpPAyrzLSGWEd-zsTFIpqj7-dvo22zthmwyxIoAIhBhtiCkqla6Uyy7S98sRhaR3f_8hhRr9Pt6VIIdSbpwK9GgKs_JKxktFizWnYx';

let client = yelp.client(API_KEY);

/* GET best match. */
router.get('/best', (req, res) => {
    var matchReq = {
        name: req.query.name,
        city: req.query.city,
        state: req.query.state,
        postal_code: req.query.postal_code,
        country: req.query.country
    };
    if(req.query.address1) {
        matchReq['address1'] = req.query.address1;
    }
    client.businessMatch('best', matchReq).then(response => {
        // res.send(response.jsonBody.businesses[0].id);
        console.log(matchReq);
        res.send(response.jsonBody);
    }).catch(e => {
        res.send(e);
    });
});

router.get('/business', (req, res) => {
    client.reviews(req.query.id).then(response => {
        res.send(response.jsonBody);
    }).catch(e => {
        res.send(e);
    });
});

module.exports = router;
