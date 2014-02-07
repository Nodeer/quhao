//
//  BaseMapViewController.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//


#import "BaseMapViewController.h"

@implementation BaseMapViewController
@synthesize mapView = _mapView;

#pragma mark - Utility

- (void)loadMapView
{
    self.mapView.frame = self.view.bounds;
    
    self.mapView.delegate = self;

    [self.view addSubview:self.mapView];
}

#pragma mark - Life Cycle

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    /* Load mapView to view hierarchy. */
    [self loadMapView];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    /* Reset map view. */
    self.mapView.visibleMapRect = MAMapRectMake(220852032.0, 101508584.0, 325416.5, 423041.4);
    
    [self.mapView removeAnnotations:self.mapView.annotations];
    
    [self.mapView removeOverlays:self.mapView.overlays];
    
    self.mapView.delegate = nil;
    
    /* Remove from view hierarchy. */
    [self.mapView removeFromSuperview];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
