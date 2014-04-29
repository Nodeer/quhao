//
//  MapViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MKAnnotation.h>
#import "CustomAnnotation.h"
#import "Helper.h"
@interface MapViewController : UIViewController<MKMapViewDelegate,CLLocationManagerDelegate>
{
    NSString *stationX;
    NSString *stationY;
}
@property (nonatomic,strong) MKMapView * myMapView;
@property (nonatomic,retain) CLLocationManager* locationManager;
@property (nonatomic,retain) CLLocation* location;
@property double x;
@property double y;
@property (strong, nonatomic) NSString *name;
@end

