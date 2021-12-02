import 'package:boba_buddy/Screens/login_page.dart';
import 'package:boba_buddy/core/repository/authentication_repository.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:provider/src/provider.dart';

class ProfilePage extends StatelessWidget {
  ProfilePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    AuthenticationRepository auth = context.read<AuthenticationRepository>();
    double deviceWidth = MediaQuery.of(context).size.width;
    List fields = ["Change Password", "Change Email", "Change Username"];
    //TODO: finish settings page -- functionality isn't crucial
    return ScreenUtilInit(
        designSize: const Size(393, 830),
        builder: () => Scaffold(
                body: Center(
                    child: SafeArea(
                        child: Column(children: [
              SafeArea(
                child: Center(
                  child: Column(
                    children: [
                      Padding(
                        padding: EdgeInsets.only(top: 80.h),
                        child: SizedBox(
                          height: 115,
                          width: 115,
                          child: Stack(
                            clipBehavior: Clip.none,
                            fit: StackFit.expand,
                            children: [
                              const CircleAvatar(
                                backgroundImage: AssetImage(
                                    "assets/images/defaultProfilePic.png"),
                              ),
                              Positioned(
                                  bottom: 0,
                                  right: -25,
                                  child: RawMaterialButton(
                                    onPressed: () {},
                                    elevation: 2.0,
                                    fillColor: const Color(0xFFF5F6F9),
                                    child: const Icon(
                                      Icons.camera_alt_outlined,
                                      color: Colors.blue,
                                      size: 20,
                                    ),
                                    padding: const EdgeInsets.all(15.0),
                                    shape: const CircleBorder(),
                                  )),
                            ],
                          ),
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.only(top: 10.h),
                        child: Text(
                          auth.currentUser.name ?? auth.currentUser.email,
                          style: const TextStyle(fontSize: 20),
                        ),
                      ),
                      Padding(
                          padding: EdgeInsets.only(top: 10.h),
                          child: Text(auth.currentUser.email)),
                      Padding(
                        padding: EdgeInsets.only(top: 60.h),
                        child: ListView.builder(
                          itemCount: fields.length,
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemBuilder: (BuildContext context, int index) {
                            return _buildField(fields[index]);
                          },
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.only(top: 40.h),
                        child: ElevatedButton(
                          onPressed: () {
                            auth.logOut();
                            var pageRoute = PageRouteBuilder(
                              pageBuilder: (c, a1, a2) => LoginPage(),
                              transitionsBuilder: (c, anim, a2, child) =>
                                  FadeTransition(opacity: anim, child: child),
                              transitionDuration:
                                  const Duration(milliseconds: 100),
                            );
                            Navigator.pushReplacement(context, pageRoute);
                          },
                          child: const Text(
                            'Logout',
                            style: TextStyle(
                                fontFamily: "Josefin Sans",
                                fontWeight: FontWeight.bold,
                                fontSize: 15),
                          ),
                          style: ElevatedButton.styleFrom(
                            shape: const RoundedRectangleBorder(
                                borderRadius:
                                    BorderRadius.all(Radius.circular(8))),
                            minimumSize: const Size(100, 40),
                            primary: const Color.fromRGBO(132, 141, 255, 1),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              )
            ])))));
  }
}

Widget _buildField(String fieldString) {
  return InkWell(
    onTap: () {
      print("tapped");
    },
    child: Container(
      height: 80.h,
      alignment: Alignment.centerLeft,
      child: Padding(
          padding: EdgeInsets.only(left: 40.w),
          child: Text(
            fieldString,
            textAlign: TextAlign.left,
          )),
    ),
  );
}
