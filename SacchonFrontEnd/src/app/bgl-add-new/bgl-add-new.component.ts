import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
// import { BglComponent } from '../bgl/bgl.component';
import { BglService } from '../services/bgl.service';
import { PatientService } from '../services/patient.service';

@Component({
  selector: 'app-bgl-add-new',
  templateUrl: './bgl-add-new.component.html',
  styleUrls: ['./bgl-add-new.component.scss']
})
export class BglAddNewComponent implements OnInit {

  response: any;
  createForm: any;



  // data =
  // {
  //   "name": "morpheus",
  //   "job": "leader"
  // }

  constructor(private service: BglService, private fb: FormBuilder, private router: Router) { }

  ngOnInit(): void {
    //  this.service.create_bgl(this.data).subscribe({
    //   next: data => this.response = data
    //  });

    this.createForm = this.fb.group({
      measurement: ["", [Validators.required]],
      date: ["", [Validators.required]],
      time: ["", [Validators.required]]
    })
  }
  redirect_add(){
    setTimeout(() => {
    this.router.navigate(['/bgl']);
  }, 100);
}

  createBGL() {
    const data = {
      id: null,
      measurement: this.createForm.get('measurement').value,
      date: this.createForm.get('date').value,
      time: this.createForm.get('time').value,
      patientId: 2
    }

    this.service.create_bgl(data).subscribe({
      next: res => this.response = res
    });
  }

}

