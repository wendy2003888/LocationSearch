
export class Category {
    constructor(
        public value: string,
        public name: string
    ) {  }
}


export class DataForm {
    constructor(
        public keyword: string,
        public category: string,
        public distance: number,
        public from: string,
        public location: string
    ) {  }
}

export class ResultEntity {
    constructor(
        public icon: string,
        public name: string,
        public place_id: string,
        public addr: string,
        public location: string
    ) {  }
}

export  class TabComponent {
    constructor(
        public title: string,
        public active: boolean,
        public content
    ){}
}

export class ReviewInstace {
    constructor(
        public author_name: string,
        public author_url: string,
        public profile_photo_url: string,
        public rating: number,
        public time: string,
        public text: string
    ) {  }
}

export const cate_values = ['default',
                'airport',
                'amusement_park',
                'aquarium',
                'art_gallery',
                'bakery',
                'bar',
                'beauty_salon',
                'bowling_alley',
                'bus_station',
                'cafe',
                'campground',
                'car_rental',
                'casino',
                'lodging',
                'movie_theater',
                'museum',
                'night_club',
                'park',
                'parking',
                'restaurant',
                'shopping_mall',
                'stadium',
                'subway_station',
                'taxi_stand',
                'train_station',
                'transit_station',
                'travel_agency',
                'zoo'];
