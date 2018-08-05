import express from 'express';
import https from "https";
// import {getReq} from './utils';

let router = express.Router();

let API_KEY = "&key=AIzaSyDrZk_oulORJtXOG2L87a2OVfH9e10JvCU";
let GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
let NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
let PAGE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=";
let DETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
let PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=750&photoreference=";
let OFFSET = 1609.34;

let loc = "34.0266,-118.2831";

// let getData = getReq();

let getData = (url, func) => {
    https.get(url, res => {
        res.setEncoding("utf8");
        let body = "";
        res.on("data", data => {
            body += data;
        });
        res.on("end", () => {
            body = JSON.parse(body);
            func(body);
        });
    });
}

let getGeocode = (url, func) =>{
    getData(url, (res_data) => {
        func(res_data);
    });
}

/* GET nearby. */
router.get('/nearby', (req, res) => {
    let keyword =  req.query.keyword;
    let category = req.query.category;
    let distance = parseFloat(req.query.distance) * OFFSET;
    let from = req.query.from;
    let location = req.query.location;
    if (from==='other') {
        let geocode_url = GEOCODING_URL + encodeURIComponent(location) + API_KEY;
        getGeocode(geocode_url, (geo_res) => {
            if (geo_res["status"] != "OK") {
                res.send(geo_res);
            } else {
                location = geo_res["results"][0]["geometry"]["location"]["lat"] + "," 
                            + geo_res["results"][0]["geometry"]["location"]["lng"];
                // $res["geo_loc"] = $location; 
                let url = NEARBY_URL + location + "&radius=" + distance + "&keyword=" + encodeURIComponent(keyword) + API_KEY;
                if (category != "default") {
                    url += "&type=" + category;
                }
                // console.log(url);
                getData(url, (res_data) => {
                    res.send(res_data);
                });
            }
        });
    } else {
        let url = NEARBY_URL + location + "&radius=" + distance + "&keyword=" + encodeURIComponent(keyword) + API_KEY;
        if (category != "default") {
            url += "&type=" + category;
        }
        getData(url, (res_data) => {
            res.send(res_data);
        });
    }
});

router.get('/page', (req, res) => {
    let url = PAGE_URL + req.query.pagetoken + API_KEY;
    getData(url, (res_data) => {
        res.send(res_data);
    });
});

router.get('/detail', (req, res) => {
    let url = DETAIL_URL +  req.query.id + API_KEY;
    getData(url, (res_data) => {
        res.send(res_data);
    });
});

router.get('/photo', (req, res) => {
    let url = PHOTO_URL + req.query.reference + API_KEY;
    getData(url, (res_data) => {
        res.send(res_data);
    });
});

module.exports = router;