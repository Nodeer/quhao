//
//  MapViewController.m
//  quHaoIos
//
//  Created by sam on 14-4-29.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "MapViewController.h"

@interface MapViewController ()

@end

@implementation MapViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)loadNavigationItem
{
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *qhBtn=[Helper getBtn:@"导 航" rect:CGRectMake( 0, 0, 40, 25 )];
    [qhBtn addTarget:self action:@selector(clickBjBtn:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *btnItem = [[UIBarButtonItem alloc] initWithCustomView:qhBtn];
    self.navigationItem.rightBarButtonItem = btnItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self loadNavigationItem];
    self.view.backgroundColor = [UIColor whiteColor];
    self.myMapView = [[MKMapView alloc] initWithFrame:self.view.bounds];
    self.myMapView.mapType = MKMapTypeStandard;
    self.myMapView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.myMapView.delegate = self;
    [self.myMapView setZoomEnabled:YES];

    //显示当前位置
    self.myMapView.showsUserLocation = YES;
    
    [self.view addSubview:self.myMapView];
    
    
    if ([CLLocationManager locationServicesEnabled]){
        self.locationManager = [[CLLocationManager alloc] init];
        self.locationManager.delegate = self;
        self.locationManager.desiredAccuracy=kCLLocationAccuracyBest;//指定需要的精度级别
        self.locationManager.distanceFilter=1000.0f;//设置距离筛选器
        [self.locationManager startUpdatingLocation];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"未开始定位服务" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    
    CLLocationCoordinate2D merchantLoc = (CLLocationCoordinate2D){self.x,self.y};
    MKCoordinateRegion theRegion = { {0.0, 0.0 }, { 0.0, 0.0 } };
    theRegion.center = merchantLoc;
    theRegion.span.longitudeDelta = 0.01f;
    theRegion.span.latitudeDelta = 0.01f;
    [self.myMapView setZoomEnabled:YES];
    [self.myMapView setScrollEnabled:YES];
    [self.myMapView setRegion:theRegion animated:YES];
    
    CustomAnnotation *ann = [[CustomAnnotation alloc] initWithCoordinates: merchantLoc title:self.name subTitle:@""];
    [self.myMapView addAnnotation:ann];
    [self.myMapView selectAnnotation:ann animated:YES];
}

-(void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
    stationX = [NSString stringWithFormat:@"%lf",userLocation.location.coordinate.latitude];
    stationY = [NSString stringWithFormat:@"%lf",userLocation.location.coordinate.longitude];
    [self.locationManager stopUpdatingLocation];
    //self.myMapView.centerCoordinate = userLocation.location.coordinate;
    
}

-(void)mapViewDidFailLoadingMap:(MKMapView *)mapView withError:(NSError *)error
{
    //NSLog(@"error : %@",error);
}

- (MKAnnotationView *)mapView:(MKMapView *)mV viewForAnnotation:(id <MKAnnotation>)annotation
{
    MKPinAnnotationView *pinView = nil;
    if(annotation != self.myMapView.userLocation)
    {
        static NSString *defaultPinID = @"com.merchant";
        pinView = (MKPinAnnotationView *)[self.myMapView dequeueReusableAnnotationViewWithIdentifier:defaultPinID];
        if ( pinView == nil ) pinView = [[MKPinAnnotationView alloc]
                                          initWithAnnotation:annotation reuseIdentifier:defaultPinID];
        pinView.pinColor = MKPinAnnotationColorRed;
        pinView.canShowCallout = YES;
        pinView.animatesDrop = YES;
    }
    else {
        [self.myMapView.userLocation setTitle:@"我的位置"];
    }
    return pinView;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];

}

-(void)clickBjBtn:(id)sender
{
    if (IOS6_SDK_AVAILABLE) {// 直接调用ios自己带的apple map
        CLLocationCoordinate2D to;
        to.latitude = self.x;
        to.longitude = self.y;
        MKMapItem *currentLocation = [MKMapItem mapItemForCurrentLocation];
        MKMapItem *toLocation = [[MKMapItem alloc] initWithPlacemark:[[MKPlacemark alloc] initWithCoordinate:to addressDictionary:nil]];
        
        toLocation.name = self.name;
        [MKMapItem openMapsWithItems:[NSArray arrayWithObjects:currentLocation, toLocation, nil]
                       launchOptions:[NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeDriving, [NSNumber numberWithBool:YES], nil]
                                      
                                      
                                                                 forKeys:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeKey, MKLaunchOptionsShowsTrafficKey, nil]]];
    } else {// ios6以下，调用google map
        NSString *urlString = [[NSString alloc]
                               initWithFormat:@"http://maps.google.com/maps?saddr=%f,%f&daddr=%@,%@&dirfl=d",
                               self.x,self.y,stationX,stationY];
        
        NSURL *aURL = [NSURL URLWithString:urlString];
        [[UIApplication sharedApplication] openURL:aURL];
    }
}
@end
