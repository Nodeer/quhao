//
//  WithiterFirstViewController.h
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIHTTPRequest.h"

@interface WithiterFirstViewController : UIViewController{
    UILabel *categoryLabel1;
    UISearchBar *searchBar1;
    UICollectionView *collectionView;
    
    UILabel *label1;
    UILabel *label2;
    UILabel *label3;
//    UILabel *label4;
    UILabel *label5;
//    UILabel *label6;
    UILabel *label7;
    UILabel *label8;
    UILabel *label9;
    UILabel *label10;
    UILabel *label11;
//    UILabel *label12;
    UILabel *label13;
    UILabel *label14;
    UILabel *label15;
    UILabel *label16;
    UILabel *label17;
    
    UIButton *btnTest;
}

@property (nonatomic, retain)
    IBOutlet UILabel *categoryLabel1;
@property (nonatomic, retain)
    IBOutlet UISearchBar *searchBar1;
@property (nonatomic, retain)
    IBOutlet UICollectionView *collectionView;

@property (nonatomic, retain)
IBOutlet UILabel *label1;
@property (nonatomic, retain)
IBOutlet UILabel *label2;
@property (nonatomic, retain)
IBOutlet UILabel *label3;
@property (nonatomic, retain)
//IBOutlet UILabel *label4;
//@property (nonatomic, retain)
IBOutlet UILabel *label5;
@property (nonatomic, retain)
//IBOutlet UILabel *label6;
//@property (nonatomic, retain)
IBOutlet UILabel *label7;
@property (nonatomic, retain)
IBOutlet UILabel *label8;
@property (nonatomic, retain)
IBOutlet UILabel *label9;
@property (nonatomic, retain)
IBOutlet UILabel *label10;
@property (nonatomic, retain)
IBOutlet UILabel *label11;
@property (nonatomic, retain)
//IBOutlet UILabel *label12;
//@property (nonatomic, retain)
IBOutlet UILabel *label13;
@property (nonatomic, retain)
IBOutlet UILabel *label14;
@property (nonatomic, retain)
IBOutlet UILabel *label15;
@property (nonatomic, retain)
IBOutlet UILabel *label16;
@property (nonatomic, retain)
IBOutlet UILabel *label17;

@property (nonatomic, retain)
IBOutlet UIButton *btnTest;

@property NSMutableArray *categoryArray;

@end
