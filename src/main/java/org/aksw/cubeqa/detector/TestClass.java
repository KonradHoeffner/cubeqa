package org.aksw.cubeqa.detector;

public class TestClass {
  public enum Company {
      Google(1),
      Microsoft(2),
      JPMorgan(3),
      WellsFargo(4);

      private int companyRatings;

      private Company(int companyValue) {
          this.companyRatings = companyValue;
      }
  }

  public static void enumComparison(Company type) {
      switch (type) {
          case Google:
              System.out.println("Company Name : " + type + " - Company Position : " + type.companyRatings);
          case Microsoft:
              System.out.println("Company Name : " + type + " - Company Position : " + type.companyRatings);
              break;
          case WellsFargo:
              System.out.println("Company Name : " + type + " - Company Position : " + type.companyRatings);
              break;
          default:
              System.out.println("Company Name : " + type + " - Company Position : " + type.companyRatings);
              break;
      }
  }

  public static void main(String[] args) {
      enumComparison(Company.Google);
  }
}