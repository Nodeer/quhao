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
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
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
    
    CLLocationCoordinate2D merchantLoc = (CLLocationCoordinate2D){self.y,self.x};
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
@end
