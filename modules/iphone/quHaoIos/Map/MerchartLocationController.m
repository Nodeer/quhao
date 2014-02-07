//
//  MerchartLocationController.m
//  quHaoIos
//
//  Created by sam on 13-9-11.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "MerchartLocationController.h"
#define TITLE @"高德地图API-2D"

@interface MerchartLocationController ()

@property (nonatomic, strong) MAPointAnnotation *centerPointAnnotation;

@end

@implementation MerchartLocationController
@synthesize centerPointAnnotation = _centerPointAnnotation;


#pragma mark - MAMapViewDelegate

- (MAAnnotationView *)mapView:(MAMapView *)mapView viewForAnnotation:(id<MAAnnotation>)annotation
{
  if ([annotation isKindOfClass:[MAPointAnnotation class]])
    {
        static NSString *pointReuseIndetifier = @"pointReuseIndetifier";
        MAPinAnnotationView *annotationView = (MAPinAnnotationView*)[mapView dequeueReusableAnnotationViewWithIdentifier:pointReuseIndetifier];
        if (annotationView == nil)
        {
            annotationView = [[MAPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:pointReuseIndetifier];
            annotationView.canShowCallout            = YES;
            annotationView.animatesDrop              = YES;
            annotationView.draggable                 = YES;
            //annotationView.rightCalloutAccessoryView = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
        }
        else
        {
            annotationView.annotation = annotation;
        }

        return annotationView;
    }
    return nil;
}

#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self refreshAction];
    self.mapView.showsUserLocation = YES;
}

- (void)viewDidDisappear:(BOOL)animated
{
    /* Reset mapView. */
    self.mapView.showsUserLocation = NO;
    self.mapView.userTrackingMode  = MAUserTrackingModeNone;


    [super viewDidDisappear:animated];
}


- (void)refreshAction
{
    [self.mapView removeAnnotations:self.mapView.annotations];
    
    self.centerPointAnnotation = [[MAPointAnnotation alloc] init];
    //self.mapView.centerCoordinate= CLLocationCoordinate2DMake(31.132877326090362,121.40267300000005 );
    self.mapView.centerCoordinate= CLLocationCoordinate2DMake(self.x,self.y );
    self.centerPointAnnotation.coordinate = self.mapView.centerCoordinate;
    self.centerPointAnnotation.title = @"商家位置";
    
    [self.mapView addAnnotation:self.centerPointAnnotation];
    
    }
@end

